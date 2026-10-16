package com.novalang.runtime.compilation;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Function0;
import com.novalang.runtime.Nova;
import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.NovaScriptContext;
import com.novalang.runtime.SchedulerHolder;
import com.novalang.runtime.ScriptCallbackContexts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

/** 程序直接拥有异步任务，关闭和跨线程上下文传播不再依赖额外资源树。 */
class ProgramAsyncLifecycleTest {
    @TempDir
    Path root;

    /** 验证关闭程序会取消 Future 并中断真实 delay，后续业务不再执行。 */
    @Test
    void programCloseInterruptsDelayAndCancelsFuture() throws Exception {
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch exited = new CountDownLatch(1);
        AtomicInteger escaped = new AtomicInteger();
        try (MainScheduler scheduler = new MainScheduler()) {
            SchedulerHolder.set(scheduler);
            Nova nova = new Nova();
            nova.defineFunction("entered", (Function0<Object>) () -> {
                entered.countDown();
                return null;
            });
            nova.defineFunction("exited", (Function0<Object>) () -> {
                exited.countDown();
                return null;
            });
            nova.defineFunction("escaped", (Function0<Object>) escaped::incrementAndGet);
            try (CompiledNova program = compile(nova,
                    "fun begin(): Future { return async { try { entered(); delay(60000); escaped() } finally { exited() } } }")) {
                CompletableFuture<?> future = (CompletableFuture<?>) program.call("begin");
                assertTrue(entered.await(5, TimeUnit.SECONDS));
                program.close();
                assertTrue(future.isCancelled());
                assertTrue(exited.await(5, TimeUnit.SECONDS));
                assertEquals(0, escaped.get());
            } finally {
                SchedulerHolder.clear();
            }
        }
    }

    /** 验证关闭程序时，已排队但未执行的 sync 不会越过生命周期边界。 */
    @Test
    void queuedSyncDoesNotRunAfterProgramClose() throws Exception {
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch unblock = new CountDownLatch(1);
        AtomicInteger calls = new AtomicInteger();
        try (MainScheduler scheduler = new MainScheduler()) {
            SchedulerHolder.set(scheduler);
            scheduler.executor.submit(() -> {
                entered.countDown();
                try {
                    unblock.await();
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                }
            });
            assertTrue(entered.await(5, TimeUnit.SECONDS));
            Nova nova = new Nova();
            nova.defineFunction("recordCall", (Function0<Object>) calls::incrementAndGet);
            try (CompiledNova program = compile(nova,
                    "fun begin(): Future { return async { sync { recordCall() } } }")) {
                CompletableFuture<?> future = (CompletableFuture<?>) program.call("begin");
                assertTrue(scheduler.mainQueued.await(5, TimeUnit.SECONDS));
                program.close();
                unblock.countDown();
                scheduler.executor.submit(() -> null).get(5, TimeUnit.SECONDS);
                assertTrue(future.isCancelled());
                assertEquals(0, calls.get());
            } finally {
                unblock.countDown();
                SchedulerHolder.clear();
            }
        }
    }

    /** 验证 async/sync 保留 Nova 调用绑定并清理线程池上下文。 */
    @Test
    void propagatesBindingsAndRestoresPoolThreadContext() throws Exception {
        try (MainScheduler scheduler = new MainScheduler()) {
            SchedulerHolder.set(scheduler);
            Nova nova = new Nova();
            nova.defineFunction("readBinding", (Function0<Object>) () -> NovaScriptContext.get("marker"));
            try (CompiledNova program = compile(nova,
                    "fun begin(): Future { return async { sync { readBinding() } } }")) {
                CompletableFuture<?> future = (CompletableFuture<?>) program.callIsolated(
                        "begin", Collections.singletonMap("marker", "kept"));
                assertEquals("kept", future.get(5, TimeUnit.SECONDS));
                assertNull(scheduler.executor.submit(ScriptCallbackContexts::current).get(5, TimeUnit.SECONDS));
                assertNull(scheduler.executor.submit(NovaScriptContext::current).get(5, TimeUnit.SECONDS));
            } finally {
                SchedulerHolder.clear();
            }
        }
    }

    /** 直接构造编译元数据，不创建磁盘配置或运行容器。 */
    private CompiledNova compile(Nova nova, String source) {
        CompilationConfig.Builder builder = new CompilationConfig.Builder();
        SourceUnit entry = SourceUnit.virtual("entry", source, null, null, 1, 0);
        return nova.compileToBytecode(entry, builder.build());
    }

    /** 使用真实线程验证主线程切换及异步任务取消。 */
    private static final class MainScheduler implements NovaScheduler, AutoCloseable {
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final ExecutorService workers = Executors.newCachedThreadPool();
        private final CountDownLatch mainQueued = new CountDownLatch(1);
        private Thread mainThread;

        /** 在专用主线程记录线程身份。 */
        private MainScheduler() throws Exception {
            executor.submit(() -> mainThread = Thread.currentThread()).get(5, TimeUnit.SECONDS);
        }

        /** 提交前发出信号，使测试可以确定 sync 已经排队。 */
        @Override
        public Executor mainExecutor() {
            return command -> {
                mainQueued.countDown();
                executor.execute(command);
            };
        }

        /** @return 本测试专属的异步执行器 */
        @Override
        public Executor asyncExecutor() {
            return workers;
        }

        /** @return 是否在测试主线程 */
        @Override
        public boolean isMainThread() {
            return Thread.currentThread() == mainThread;
        }

        /** 当前测试不使用定时任务。 */
        @Override
        public Cancellable scheduleLater(long delayMs, Runnable task) {
            throw new UnsupportedOperationException("Timer is not used");
        }

        /** 当前测试不使用循环定时任务。 */
        @Override
        public Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
            throw new UnsupportedOperationException("Timer is not used");
        }

        /** 结束测试专属线程池，避免失败用例遗留线程。 */
        @Override
        public void close() {
            workers.shutdownNow();
            executor.shutdownNow();
        }
    }
}
