package com.novalang.workspace;

import com.novalang.runtime.Function0;
import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

/** 真正跨线程执行编译后的 async/sync，禁止脚本手动恢复宿主绑定。 */
class WorkspaceAsyncContextTest {
    @TempDir Path directory;

    @Test
    void scopeDisposalInterruptsDelayWithoutContinuingBusiness() throws Exception {
        MainScheduler scheduler = new MainScheduler();
        SchedulerHolder.set(scheduler);
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch exited = new CountDownLatch(1);
        AtomicInteger escaped = new AtomicInteger();
        WorkspaceTestSupport.write(directory, "main.nova",
                "fun main() {}\nfun begin(): Future { return async { try { entered(); delay(60000); escaped() } finally { exited() } } }\n");
        Path config = WorkspaceTestSupport.writeConfig(directory, "caller", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> {
            nova.defineFunction("entered", (Function0<Object>) () -> { entered.countDown(); return null; });
            nova.defineFunction("exited", (Function0<Object>) () -> { exited.countDown(); return null; });
            nova.defineFunction("escaped", (Function0<Object>) () -> escaped.incrementAndGet());
        });
        try {
            workspace.load();
            ResourceScope scope = workspace.getGeneration().getRootScope().openChild(ScopeType.STAGE, "opening");
            CompletableFuture<?> result = (CompletableFuture<?>) workspace.invoke("main", "begin", Collections.emptyMap(), scope);
            assertTrue(entered.await(5, TimeUnit.SECONDS));
            scope.dispose();
            assertTrue(result.isCancelled());
            assertTrue(exited.await(5, TimeUnit.SECONDS), "实际异步线程应结束等待");
            assertEquals(0, escaped.get());
            ResourceScope next = workspace.getGeneration().getRootScope().openChild(ScopeType.STAGE, "opening");
            assertNotSame(scope, next);
            assertEquals(0, next.getResourceCount());
        } finally {
            workspace.dispose();
            scheduler.close();
            SchedulerHolder.clear();
        }
    }

    @Test
    void queuedSyncCannotRunAfterOwnerDisposal() throws Exception {
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch unblock = new CountDownLatch(1);
        AtomicInteger calls = new AtomicInteger();
        try (MainScheduler scheduler = new MainScheduler()) {
            SchedulerHolder.set(scheduler);
            scheduler.executor.submit(() -> { entered.countDown(); try { unblock.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } });
            assertTrue(entered.await(5, TimeUnit.SECONDS));
            WorkspaceTestSupport.write(directory, "main.nova",
                    "fun main() {}\nfun begin(): Future { return async { sync { recordCall() } } }\n");
            Path config = WorkspaceTestSupport.writeConfig(directory, "caller", "  - \"main\"\n");
            RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> nova.defineFunction("recordCall", (Function0<Object>) () -> calls.incrementAndGet()));
            try {
                workspace.load();
                ResourceScope scope = workspace.getGeneration().getRootScope().openChild(ScopeType.STAGE, "opening");
                CompletableFuture<?> result = (CompletableFuture<?>) workspace.invoke("main", "begin", Collections.emptyMap(), scope);
                long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
                while (scope.getResourceCount() < 2 && System.nanoTime() < deadline) {
                    Thread.sleep(1);
                }
                assertEquals(2, scope.getResourceCount(), "异步任务和排队 sync 都必须归属 Scope");
                scope.dispose();
                unblock.countDown();
                assertFalse(scheduler.executor.submit(() -> Thread.currentThread().isInterrupted()).get(5, TimeUnit.SECONDS));
                assertTrue(result.isCancelled());
                assertEquals(0, calls.get());
            } finally {
                unblock.countDown();
                workspace.dispose();
                SchedulerHolder.clear();
            }
        }
    }

    @Test
    void propagatesBindingsAcrossAsyncAndSync() throws Exception {
        try (MainScheduler scheduler = new MainScheduler()) {
            SchedulerHolder.set(scheduler);
            WorkspaceTestSupport.write(directory, "main.nova",
                    "fun main() {}\nfun begin(): Future { return async { sync { readBinding() } } }\n");
            Path config = WorkspaceTestSupport.writeConfig(directory, "caller", "  - \"main\"\n");
            RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> nova.defineFunction(
                    "readBinding", (Function0<Object>) () -> WorkspaceExecutionContext.currentBindings().get("marker")));
            try {
                workspace.load();
                ResourceScope scope = workspace.getGeneration().getRootScope().openChild(ScopeType.STAGE, "opening");
                CompletableFuture<?> result = (CompletableFuture<?>) workspace.invoke("main", "begin", Collections.singletonMap("marker", "kept"), scope);
                assertEquals("kept", result.get(5, TimeUnit.SECONDS));
                assertNull(scheduler.executor.submit(() -> WorkspaceExecutionContext.currentScope()).get(5, TimeUnit.SECONDS));
            } finally {
                workspace.dispose();
                SchedulerHolder.clear();
            }
        }
    }

    static final class MainScheduler implements NovaScheduler, AutoCloseable {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        volatile Thread mainThread;
        MainScheduler() throws Exception {
            executor.submit(() -> mainThread = Thread.currentThread()).get(5, TimeUnit.SECONDS);
        }
        public Executor mainExecutor() { return executor; }
        public Executor asyncExecutor() { return ForkJoinPool.commonPool(); }
        public boolean isMainThread() { return Thread.currentThread() == mainThread; }
        public Cancellable scheduleLater(long milliseconds, Runnable task) { throw new UnsupportedOperationException(); }
        public Cancellable scheduleRepeat(long delay, long period, Runnable task) { throw new UnsupportedOperationException(); }
        public void close() { executor.shutdownNow(); }
    }
}
