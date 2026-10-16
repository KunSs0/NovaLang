package com.novalang.runtime.compilation;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Function0;
import com.novalang.runtime.Nova;
import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import org.junit.jupiter.api.Test;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

/** 直接验证普通 Nova 程序对定时任务的所有权，单文件编译也使用同一套能力。 */
class ProgramSchedulingTest {
    /** 单次任务结束即解除登记，关闭程序不会再次取消已经完成的任务。 */
    @Test
    void completedOneShotDetachesFromProgram() {
        CapturingScheduler scheduler = new CapturingScheduler();
        SchedulerHolder.set(scheduler);
        AtomicInteger calls = new AtomicInteger();
        Nova nova = new Nova();
        nova.defineFunction("record", (Function0<Object>) calls::incrementAndGet);
        try (CompiledNova program = nova.compileToBytecode("schedule(50) { record() }")) {
            program.run();
            assertFalse(scheduler.handle.cancelled);
            scheduler.action.run();
            assertEquals(1, calls.get());
            assertTrue(scheduler.handle.cancelled);
            assertEquals(1, scheduler.handle.cancellations);
            program.close();
            assertEquals(1, scheduler.handle.cancellations);
        } finally {
            SchedulerHolder.clear();
        }
    }

    /** 关闭程序会取消循环任务，宿主残留的执行器引用也不能再次执行脚本。 */
    @Test
    void programCloseCancelsRepeatingTask() {
        CapturingScheduler scheduler = new CapturingScheduler();
        SchedulerHolder.set(scheduler);
        AtomicInteger calls = new AtomicInteger();
        Nova nova = new Nova();
        nova.defineFunction("record", (Function0<Object>) calls::incrementAndGet);
        try (CompiledNova program = nova.compileToBytecode("scheduleRepeat(50, 50) { record() }")) {
            program.run();
            scheduler.action.run();
            assertEquals(1, calls.get());
            program.close();
            assertTrue(scheduler.handle.cancelled);
            scheduler.action.run();
            assertEquals(1, calls.get());
        } finally {
            SchedulerHolder.clear();
        }
    }

    /** 宿主在返回句柄前立即执行任务时，完成状态仍能正确取消实际句柄。 */
    @Test
    void handlesSynchronousSchedulerCompletion() {
        CapturingScheduler scheduler = new CapturingScheduler();
        scheduler.immediate = true;
        SchedulerHolder.set(scheduler);
        try (CompiledNova program = new Nova().compileToBytecode("schedule(0) { 7 }")) {
            program.run();
            assertTrue(scheduler.handle.cancelled);
            assertEquals(1, scheduler.handle.cancellations);
        } finally {
            SchedulerHolder.clear();
        }
    }

    /** 保存原始回调及取消次数，使测试可覆盖执行完成和关闭竞态。 */
    private static final class CapturingScheduler implements NovaScheduler {
        private Runnable action;
        private Handle handle;
        private boolean immediate;

        /** 当前测试线程即主线程，不启动额外线程。 */
        @Override
        public Executor mainExecutor() {
            return Runnable::run;
        }

        /** 当前测试只验证定时任务所有权。 */
        @Override
        public Executor asyncExecutor() {
            return Runnable::run;
        }

        /** @return 本测试调用始终在主线程 */
        @Override
        public boolean isMainThread() {
            return true;
        }

        /** 根据测试模式保存或立即执行回调。 */
        @Override
        public Cancellable scheduleLater(long delayMs, Runnable task) {
            action = task;
            handle = new Handle();
            if (immediate) {
                task.run();
            }
            return handle;
        }

        /** 循环任务同样由测试显式触发。 */
        @Override
        public Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
            return scheduleLater(delayMs, task);
        }

        /** 记录实际宿主任务收到的取消请求。 */
        private static final class Handle implements Cancellable {
            private boolean cancelled;
            private int cancellations;

            /** 保存取消状态和调用次数。 */
            @Override
            public void cancel() {
                cancelled = true;
                cancellations++;
            }

            /** @return 宿主任务取消状态 */
            @Override
            public boolean isCancelled() {
                return cancelled;
            }
        }
    }
}
