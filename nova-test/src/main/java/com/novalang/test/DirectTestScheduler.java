package com.novalang.test;

import com.novalang.runtime.NovaScheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/** CLI 和底层测试使用的直接调度器。 */
public final class DirectTestScheduler implements NovaScheduler, AutoCloseable {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    @Override
    public Executor mainExecutor() {
        return Runnable::run;
    }

    @Override
    public Executor asyncExecutor() {
        return executor;
    }

    @Override
    public boolean isMainThread() {
        return true;
    }

    @Override
    public Cancellable scheduleLater(long delayMs, Runnable task) {
        return new FutureCancellable(executor.schedule(task, delayMs, TimeUnit.MILLISECONDS));
    }

    @Override
    public Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
        return new FutureCancellable(executor.scheduleAtFixedRate(
                task, delayMs, periodMs, TimeUnit.MILLISECONDS));
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }

    private static final class FutureCancellable implements Cancellable {
        private final ScheduledFuture<?> future;

        private FutureCancellable(ScheduledFuture<?> future) {
            this.future = future;
        }

        @Override
        public void cancel() {
            future.cancel(false);
        }

        @Override
        public boolean isCancelled() {
            return future.isCancelled();
        }
    }
}
