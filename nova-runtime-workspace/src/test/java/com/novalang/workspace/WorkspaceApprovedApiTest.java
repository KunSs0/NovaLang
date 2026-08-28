package com.novalang.workspace;

import com.novalang.runtime.Function0;
import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import com.novalang.runtime.interpreter.Interpreter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 经批准的 Workspace 回调、调度和配置通用 API 集成测试。
 */
@DisplayName("Workspace 通用脚本 API")
class WorkspaceApprovedApiTest {

    @TempDir
    Path tempDirectory;

    /**
     * 每个测试后清除进程级调度器状态。
     */
    @AfterEach
    void clearScheduler() {
        Interpreter.resetGlobalSchedulerState();
    }

    @Test
    @DisplayName("一次性任务归属 Scope 且执行完成后解除登记")
    void shouldOwnAndReleaseOneShotTask() throws Exception {
        CapturingScheduler scheduler = new CapturingScheduler();
        SchedulerHolder.set(scheduler);
        AtomicInteger calls = new AtomicInteger();
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "fun main() { schedule(50) { recordCall() } }");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> nova.defineFunction(
                "recordCall", new Function0<Object>() {
                    @Override
                    public Object invoke() {
                        calls.incrementAndGet();
                        return null;
                    }
                }));
        try {
            workspace.load();
            assertEquals(1, workspace.getGeneration().getRootScope().getResourceCount());
            assertEquals(50L, scheduler.laterDelayMs);

            scheduler.runLater();

            assertEquals(1, calls.get());
            assertEquals(0, workspace.getGeneration().getRootScope().getResourceCount());
            assertTrue(scheduler.laterHandle.isCancelled());
        } finally {
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("循环任务在 Workspace dispose 时取消")
    void shouldCancelRepeatingTaskOnDispose() throws Exception {
        CapturingScheduler scheduler = new CapturingScheduler();
        SchedulerHolder.set(scheduler);
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "fun main() { scheduleRepeat(50, 50) { } }");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> { });
        workspace.load();
        assertFalse(scheduler.repeatHandle.isCancelled());
        assertEquals(50L, scheduler.repeatDelayMs);
        assertEquals(50L, scheduler.repeatPeriodMs);

        workspace.dispose();

        assertTrue(scheduler.repeatHandle.isCancelled());
    }

    @Test
    @DisplayName("原生延迟任务在 Workspace dispose 后不再执行")
    void shouldRejectNativeScheduledCallbackAfterWorkspaceDispose() throws Exception {
        CapturingScheduler scheduler = new CapturingScheduler();
        SchedulerHolder.set(scheduler);
        AtomicInteger calls = new AtomicInteger();
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "fun main() { schedule(50) { recordCall() } }");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> nova.defineFunction(
                "recordCall", new Function0<Object>() {
                    @Override
                    public Object invoke() {
                        calls.incrementAndGet();
                        return null;
                    }
                }));
        workspace.load();

        workspace.dispose();
        scheduler.runLater();

        assertEquals(0, calls.get());
        assertTrue(scheduler.laterHandle.isCancelled());
    }

    @Test
    @DisplayName("配置读取限定 Workspace 根并执行严格类型检查")
    void shouldReadStrictYamlInsideWorkspaceRoot() throws Exception {
        SchedulerHolder.set(WorkspaceTestSupport.directScheduler());
        WorkspaceTestSupport.write(tempDirectory, "config/rules.yml",
                "combat:\n  cost: 7\n  enabled: true\n  name: \"战斗配置\"\n");
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "val rules = Java.static(\"com.novalang.workspace.WorkspaceConfigFiles\", \"loadYaml\", \"config/rules.yml\")\n"
                        + "fun cost(): Int = Java.static(\"com.novalang.workspace.WorkspaceConfigFiles\", \"readInt\", rules, \"combat.cost\")\n"
                        + "fun name(): String = Java.static(\"com.novalang.workspace.WorkspaceConfigFiles\", \"readString\", rules, \"combat.name\")\n"
                        + "fun wrongType(): Int = Java.static(\"com.novalang.workspace.WorkspaceConfigFiles\", \"readInt\", rules, \"combat.enabled\")\n"
                        + "fun escape(): Any = Java.static(\"com.novalang.workspace.WorkspaceConfigFiles\", \"loadYaml\", \"../outside.yml\")");
        Path config = WorkspaceTestSupport.writeConfig(tempDirectory, "caller", "  - \"main\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(config, nova -> { });
        try {
            workspace.load();

            assertEquals(7, ((Number) workspace.invoke("main", "cost",
                    Collections.<String, Object>emptyMap(), null)).intValue());
            assertEquals("战斗配置", workspace.invoke("main", "name",
                    Collections.<String, Object>emptyMap(), null));
            WorkspaceException typeFailure = assertThrows(WorkspaceException.class,
                    () -> workspace.invoke("main", "wrongType",
                            Collections.<String, Object>emptyMap(), null));
            assertTrue(describeFailure(typeFailure).contains("must be an integer"));
            WorkspaceException escapeFailure = assertThrows(WorkspaceException.class,
                    () -> workspace.invoke("main", "escape",
                            Collections.<String, Object>emptyMap(), null));
            assertTrue(describeFailure(escapeFailure).contains("escapes the root directory"));
        } finally {
            workspace.dispose();
        }
    }

    /**
     * 将异常链转换为便于断言的文本。
     *
     * @param failure 根异常
     * @return 异常链文本
     */
    private String describeFailure(Throwable failure) {
        StringBuilder result = new StringBuilder();
        Throwable current = failure;
        while (current != null) {
            if (result.length() > 0) {
                result.append(" -> ");
            }
            result.append(current.getMessage());
            current = current.getCause();
        }
        return result.toString();
    }

    /**
     * 捕获任务并允许测试显式触发的调度器。
     */
    private static final class CapturingScheduler implements NovaScheduler {
        private Runnable laterTask;
        private Runnable repeatTask;
        private long laterDelayMs;
        private long repeatDelayMs;
        private long repeatPeriodMs;
        private final Handle laterHandle = new Handle();
        private final Handle repeatHandle = new Handle();

        /** {@inheritDoc} */
        @Override
        public Executor mainExecutor() {
            return Runnable::run;
        }

        /** {@inheritDoc} */
        @Override
        public Executor asyncExecutor() {
            return Runnable::run;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isMainThread() {
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public Cancellable scheduleLater(long delayMs, Runnable task) {
            laterDelayMs = delayMs;
            laterTask = task;
            return laterHandle;
        }

        /** {@inheritDoc} */
        @Override
        public Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
            repeatDelayMs = delayMs;
            repeatPeriodMs = periodMs;
            repeatTask = task;
            return repeatHandle;
        }

        /**
         * 执行已捕获的一次性任务。
         */
        void runLater() {
            if (laterTask == null) {
                throw new IllegalStateException("No one-shot task was scheduled");
            }
            laterTask.run();
        }
    }

    /**
     * 测试调度器使用的可取消句柄。
     */
    private static final class Handle implements NovaScheduler.Cancellable {
        private boolean cancelled;

        /** {@inheritDoc} */
        @Override
        public void cancel() {
            cancelled = true;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isCancelled() {
            return cancelled;
        }
    }
}
