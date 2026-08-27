package com.novalang.workspace;

import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;

/**
 * 将 Nova 调度任务绑定到当前 Workspace ResourceScope 的通用入口。
 */
public final class WorkspaceTasks {

    /**
     * 工具类不允许实例化。
     */
    private WorkspaceTasks() {
    }

    /**
     * 使用 Workspace 默认策略延迟执行一次 Nova 函数。
     *
     * @param delayMillis 延迟毫秒数
     * @param entryName 函数所属入口名称
     * @param functionName 函数名称
     * @return 可主动取消且自动归属当前作用域的任务句柄
     */
    public static NovaScheduler.Cancellable later(long delayMillis,
                                                   String entryName,
                                                   String functionName) {
        return later(delayMillis, entryName, functionName, null);
    }

    /**
     * 使用指定执行策略延迟执行一次 Nova 函数。
     *
     * @param delayMillis 延迟毫秒数
     * @param entryName 函数所属入口名称
     * @param functionName 函数名称
     * @param policy 固定执行策略；传 {@code null} 时使用 Workspace 默认策略
     * @return 可主动取消且自动归属当前作用域的任务句柄
     */
    public static NovaScheduler.Cancellable later(long delayMillis,
                                                   String entryName,
                                                   String functionName,
                                                   ExecutionPolicy policy) {
        if (delayMillis < 0L) {
            throw new IllegalArgumentException("delayMillis must not be negative");
        }
        return schedule(false, delayMillis, 0L, entryName, functionName, policy);
    }

    /**
     * 使用 Workspace 默认策略重复执行 Nova 函数。
     *
     * @param delayMillis 首次执行前的延迟毫秒数
     * @param periodMillis 重复周期毫秒数
     * @param entryName 函数所属入口名称
     * @param functionName 函数名称
     * @return 可主动取消且自动归属当前作用域的任务句柄
     */
    public static NovaScheduler.Cancellable repeat(long delayMillis,
                                                    long periodMillis,
                                                    String entryName,
                                                    String functionName) {
        return repeat(delayMillis, periodMillis, entryName, functionName, null);
    }

    /**
     * 使用指定执行策略重复执行 Nova 函数。
     *
     * @param delayMillis 首次执行前的延迟毫秒数
     * @param periodMillis 重复周期毫秒数
     * @param entryName 函数所属入口名称
     * @param functionName 函数名称
     * @param policy 固定执行策略；传 {@code null} 时使用 Workspace 默认策略
     * @return 可主动取消且自动归属当前作用域的任务句柄
     */
    public static NovaScheduler.Cancellable repeat(long delayMillis,
                                                    long periodMillis,
                                                    String entryName,
                                                    String functionName,
                                                    ExecutionPolicy policy) {
        if (delayMillis < 0L) {
            throw new IllegalArgumentException("delayMillis must not be negative");
        }
        if (periodMillis <= 0L) {
            throw new IllegalArgumentException("periodMillis must be greater than zero");
        }
        return schedule(true, delayMillis, periodMillis, entryName, functionName, policy);
    }

    /**
     * 创建稳定回调、提交调度任务并登记到当前作用域。
     *
     * @param repeating 是否为循环任务
     * @param delayMillis 首次执行延迟
     * @param periodMillis 循环周期
     * @param entryName 函数所属入口名称
     * @param functionName 函数名称
     * @param policy 固定执行策略
     * @return 已登记的任务句柄
     */
    private static NovaScheduler.Cancellable schedule(boolean repeating,
                                                       long delayMillis,
                                                       long periodMillis,
                                                       String entryName,
                                                       String functionName,
                                                       ExecutionPolicy policy) {
        NovaScheduler scheduler = SchedulerHolder.get();
        if (scheduler == null) {
            throw new WorkspaceException("The global NovaScheduler is not installed");
        }
        ResourceScope scope = WorkspaceExecutionContext.requireScope();
        NovaCallback callback = WorkspaceCallbacks.create(entryName, functionName, policy);
        ScopedTask task = new ScopedTask(scope, callback, repeating);
        NovaScheduler.Cancellable scheduled;
        if (repeating) {
            scheduled = scheduler.scheduleRepeat(delayMillis, periodMillis, task::execute);
        } else {
            scheduled = scheduler.scheduleLater(delayMillis, task::execute);
        }
        task.bind(scheduled);
        try {
            scope.register(task);
            task.markRegistered();
            return task;
        } catch (RuntimeException exception) {
            task.cancel();
            throw exception;
        }
    }

    /**
     * 同时实现 Nova 调度句柄和 Workspace 资源的任务包装器。
     */
    private static final class ScopedTask implements NovaScheduler.Cancellable, WorkspaceResource {
        private final ResourceScope scope;
        private final NovaCallback callback;
        private final boolean repeating;
        private NovaScheduler.Cancellable delegate;
        private boolean registered;
        private boolean completed;
        private boolean cancelled;

        ScopedTask(ResourceScope scope, NovaCallback callback, boolean repeating) {
            this.scope = scope;
            this.callback = callback;
            this.repeating = repeating;
        }

        /**
         * 绑定宿主调度器创建的实际句柄。
         *
         * @param scheduled 实际调度句柄
         */
        synchronized void bind(NovaScheduler.Cancellable scheduled) {
            delegate = scheduled;
            if (cancelled || completed) {
                scheduled.cancel();
            }
        }

        /**
         * 标记资源已进入作用域，并处理同步完成的调度器竞态。
         */
        synchronized void markRegistered() {
            registered = true;
            if (completed || cancelled) {
                scope.unregister(this);
            }
        }

        /**
         * 由宿主调度器触发 Nova 稳定回调。
         */
        void execute() {
            synchronized (this) {
                if (cancelled || completed) {
                    return;
                }
                if (!callback.isValid()) {
                    cancel();
                    return;
                }
            }
            try {
                callback.invoke();
            } finally {
                if (!repeating) {
                    complete();
                }
            }
        }

        /**
         * 标记一次性任务完成并释放作用域和调度句柄引用。
         */
        private synchronized void complete() {
            if (completed) {
                return;
            }
            completed = true;
            NovaScheduler.Cancellable current = delegate;
            delegate = null;
            if (current != null) {
                current.cancel();
            }
            if (registered) {
                scope.unregister(this);
            }
        }

        /**
         * 取消任务并解除作用域登记。
         */
        @Override
        public synchronized void cancel() {
            if (cancelled) {
                return;
            }
            cancelled = true;
            NovaScheduler.Cancellable current = delegate;
            delegate = null;
            if (current != null) {
                current.cancel();
            }
            if (registered) {
                scope.unregister(this);
            }
        }

        /**
         * 返回任务是否已经取消或完成。
         *
         * @return 不再接受执行时返回 {@code true}
         */
        @Override
        public synchronized boolean isCancelled() {
            return cancelled || completed;
        }

        /**
         * Workspace dispose 入口，等价于主动取消。
         */
        @Override
        public void dispose() {
            cancel();
        }
    }
}
