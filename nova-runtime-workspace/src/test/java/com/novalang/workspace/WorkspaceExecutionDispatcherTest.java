package com.novalang.workspace;

import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link WorkspaceExecutionDispatcher} 固定线程策略测试。
 */
@DisplayName("Workspace 执行策略")
class WorkspaceExecutionDispatcherTest {

    @AfterEach
    void clearScheduler() {
        SchedulerHolder.clear();
    }

    @Test
    @DisplayName("MAIN_THREAD 将调用派发到宿主主线程")
    void shouldDispatchToHostMainThread() throws Exception {
        ExecutorService mainExecutor = Executors.newSingleThreadExecutor();
        AtomicReference<Thread> mainThread = new AtomicReference<Thread>();
        try {
            Future<?> initialization = mainExecutor.submit(() -> mainThread.set(Thread.currentThread()));
            initialization.get(5, TimeUnit.SECONDS);
            SchedulerHolder.set(scheduler(mainExecutor, mainThread));
            ResourceScope scope = ResourceScope.generation("main-dispatch");

            Thread executed = WorkspaceExecutionDispatcher.execute(
                    ExecutionPolicy.MAIN_THREAD, scope, Thread::currentThread);

            assertEquals(mainThread.get(), executed);
            scope.dispose();
        } finally {
            mainExecutor.shutdownNow();
        }
    }

    @Test
    @DisplayName("CALLER_THREAD 不会自动切换到主线程")
    void shouldStayOnCallerThread() {
        ResourceScope scope = ResourceScope.generation("caller-dispatch");
        Thread caller = Thread.currentThread();

        Thread executed = WorkspaceExecutionDispatcher.execute(
                ExecutionPolicy.CALLER_THREAD, scope, Thread::currentThread);

        assertEquals(caller, executed);
        scope.dispose();
    }

    @Test
    @DisplayName("MAIN_THREAD 缺少 mainExecutor 时直接失败")
    void shouldRejectMissingMainExecutor() {
        AtomicReference<Thread> impossibleMain = new AtomicReference<Thread>();
        impossibleMain.set(new Thread("never-current"));
        SchedulerHolder.set(scheduler(null, impossibleMain));
        ResourceScope scope = ResourceScope.generation("missing-main-executor");

        WorkspaceException exception = assertThrows(WorkspaceException.class,
                () -> WorkspaceExecutionDispatcher.execute(
                        ExecutionPolicy.MAIN_THREAD, scope, () -> 1));

        assertEquals("The global NovaScheduler has no main executor", exception.getMessage());
        scope.dispose();
    }

    private NovaScheduler scheduler(Executor executor, AtomicReference<Thread> mainThread) {
        return new NovaScheduler() {
            @Override
            public Executor mainExecutor() {
                return executor;
            }

            @Override
            public boolean isMainThread() {
                return Thread.currentThread() == mainThread.get();
            }

            @Override
            public Cancellable scheduleLater(long delayMs, Runnable task) {
                throw new UnsupportedOperationException("Scheduling is not used by this test");
            }

            @Override
            public Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
                throw new UnsupportedOperationException("Scheduling is not used by this test");
            }
        };
    }
}
