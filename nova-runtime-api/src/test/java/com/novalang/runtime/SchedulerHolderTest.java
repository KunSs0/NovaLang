package com.novalang.runtime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link SchedulerHolder} 进程级单例约束测试。
 */
@DisplayName("SchedulerHolder singleton")
class SchedulerHolderTest {

    /**
     * 每个测试后清理进程级状态。
     */
    @AfterEach
    void clearScheduler() {
        SchedulerHolder.clear();
    }

    /**
     * 验证同一实例可以重复确认，而不同实例不能覆盖宿主调度器。
     */
    @Test
    @DisplayName("rejects replacing the installed scheduler")
    void shouldRejectDifferentScheduler() {
        NovaScheduler first = new DirectScheduler();
        NovaScheduler second = new DirectScheduler();

        SchedulerHolder.set(first);
        assertDoesNotThrow(() -> SchedulerHolder.set(first));
        IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> SchedulerHolder.set(second));

        assertEquals("A different Nova scheduler has already been installed.",
                exception.getMessage());
        assertSame(first, SchedulerHolder.get());
    }

    /**
     * 验证空调度器不能作为清理操作传入安装入口。
     */
    @Test
    @DisplayName("rejects a null scheduler")
    void shouldRejectNullScheduler() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> SchedulerHolder.set(null));

        assertEquals("The Nova scheduler must not be null.", exception.getMessage());
    }

    /**
     * 测试使用的直接执行调度器。
     */
    private static final class DirectScheduler implements NovaScheduler {

        /**
         * {@inheritDoc}
         */
        @Override
        public Executor mainExecutor() {
            return Runnable::run;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isMainThread() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Cancellable scheduleLater(long delayMs, Runnable task) {
            throw new UnsupportedOperationException("Scheduling is not used by this test.");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
            throw new UnsupportedOperationException("Scheduling is not used by this test.");
        }
    }
}
