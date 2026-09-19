package com.novalang.runtime.stdlib;

import com.novalang.runtime.NovaScheduler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/** Scope 取消时同时结束 Future 并中断实际等待线程，禁止遗留后台 sleep。 */
final class OwnedAsyncTask extends CompletableFuture<Object> implements NovaScheduler.Cancellable {
    private final CapturedTaskContext context = new CapturedTaskContext();
    private Thread worker;
    private boolean interruptible;

    static OwnedAsyncTask submit(Supplier<Object> action, Executor executor) {
        return submit(action, executor, true);
    }

    static OwnedAsyncTask submit(Supplier<Object> action, Executor executor, boolean interruptible) {
        OwnedAsyncTask task = new OwnedAsyncTask();
        task.interruptible = interruptible;
        try {
            AutoCloseable registration = task.context.owner == null ? null : task.context.owner.registerTask(task);
            task.whenComplete((value, error) -> {
                if (registration != null) {
                    try {
                        registration.close();
                    } catch (Exception exception) {
                        java.util.logging.Logger.getLogger("Nova").log(java.util.logging.Level.SEVERE, "Cannot detach async task", exception);
                    }
                }
            });
            executor.execute(() -> task.execute(action));
            return task;
        } catch (RuntimeException exception) {
            task.completeExceptionally(exception);
            throw exception;
        }
    }

    private void execute(Supplier<Object> action) {
        synchronized (this) {
            if (isDone()) {
                return;
            }
            worker = Thread.currentThread();
        }
        try {
            complete(context.call(action));
        } catch (Throwable exception) {
            completeExceptionally(exception);
        } finally {
            synchronized (this) {
                worker = null;
                if (interruptible && isCancelled()) {
                    Thread.interrupted();
                }
            }
        }
    }

    @Override
    public synchronized boolean cancel(boolean interrupt) {
        boolean cancelled = super.cancel(interrupt);
        if (cancelled && interrupt && interruptible && worker != null) {
            worker.interrupt();
        }
        return cancelled;
    }

    @Override
    public void cancel() {
        cancel(true);
    }
}
