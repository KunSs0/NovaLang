package com.novalang.workspace;

import com.novalang.runtime.NovaScheduleContext;
import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Workspace 调用期间安装的原生调度任务上下文。
 *
 * <p>它只承接 {@code schedule}/{@code scheduleRepeat} 的资源归属和回调上下文恢复；
 * 实际线程和时间调度仍完全由宿主 {@link NovaScheduler} 决定。</p>
 */
final class WorkspaceScheduleContext implements NovaScheduleContext {

    private final WorkspaceGeneration generation;
    private final ResourceScope scope;
    private final Map<String, Object> capturedBindings;

    /**
     * 创建当前 Workspace 调用的任务上下文。
     *
     * @param generation 当前代际
     * @param scope 当前资源作用域
     * @param bindings 当前调用绑定快照
     */
    WorkspaceScheduleContext(WorkspaceGeneration generation,
                             ResourceScope scope,
                             Map<String, Object> bindings) {
        this.generation = generation;
        this.scope = scope;
        this.capturedBindings = Collections.unmodifiableMap(
                new LinkedHashMap<String, Object>(bindings));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NovaScheduler.Cancellable scheduleLater(long delayMs, Runnable task) {
        return schedule(false, delayMs, 0L, task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NovaScheduler.Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
        return schedule(true, delayMs, periodMs, task);
    }

    /**
     * 创建并登记受当前 Scope 管理的宿主调度任务。
     *
     * @param repeating 是否循环执行
     * @param delayMs 首次延迟毫秒数
     * @param periodMs 循环周期毫秒数
     * @param task Nova 回调
     * @return 可取消句柄
     */
    private NovaScheduler.Cancellable schedule(boolean repeating,
                                                long delayMs,
                                                long periodMs,
                                                Runnable task) {
        NovaScheduler scheduler = SchedulerHolder.get();
        if (scheduler == null) {
            throw new WorkspaceException("The global NovaScheduler is not installed");
        }
        WorkspaceScheduledTask scopedTask = new WorkspaceScheduledTask(
                generation, scope, capturedBindings, task, repeating);
        NovaScheduler.Cancellable scheduled;
        if (repeating) {
            scheduled = scheduler.scheduleRepeat(delayMs, periodMs, scopedTask::execute);
        } else {
            scheduled = scheduler.scheduleLater(delayMs, scopedTask::execute);
        }
        scopedTask.bind(scheduled);
        try {
            scope.register(scopedTask);
            scopedTask.markRegistered();
            return scopedTask;
        } catch (RuntimeException exception) {
            scopedTask.cancel();
            throw exception;
        }
    }

    /**
     * 同时实现宿主取消句柄和 Workspace 资源的受管任务。
     */
    private static final class WorkspaceScheduledTask implements NovaScheduler.Cancellable, WorkspaceResource {
        private final WorkspaceGeneration generation;
        private final ResourceScope scope;
        private Map<String, Object> capturedBindings;
        private Runnable callback;
        private final boolean repeating;
        private NovaScheduler.Cancellable delegate;
        private boolean registered;
        private boolean completed;
        private boolean cancelled;

        WorkspaceScheduledTask(WorkspaceGeneration generation,
                               ResourceScope scope,
                               Map<String, Object> capturedBindings,
                               Runnable callback,
                               boolean repeating) {
            this.generation = generation;
            this.scope = scope;
            this.capturedBindings = capturedBindings;
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
         * 标记任务已经登记到资源作用域。
         */
        synchronized void markRegistered() {
            registered = true;
            if (completed || cancelled) {
                scope.unregister(this);
            }
        }

        /**
         * 由宿主调度器触发并恢复 Workspace 上下文。
         */
        void execute() {
            Map<String, Object> bindings;
            Runnable task;
            synchronized (this) {
                if (cancelled || completed) {
                    return;
                }
                if (!generation.isActive() || scope.getState() != ResourceScopeState.ACTIVE) {
                    cancel();
                    return;
                }
                bindings = capturedBindings;
                task = callback;
                if (bindings == null || task == null) {
                    return;
                }
            }
            try {
                generation.executeScheduledCallback(scope, bindings, task);
            } finally {
                if (!repeating) {
                    complete();
                }
            }
        }

        /**
         * 标记一次性任务结束并解除资源登记。
         */
        private synchronized void complete() {
            if (completed) {
                return;
            }
            completed = true;
            NovaScheduler.Cancellable current = delegate;
            delegate = null;
            capturedBindings = null;
            callback = null;
            if (current != null) {
                current.cancel();
            }
            if (registered) {
                scope.unregister(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized void cancel() {
            if (cancelled) {
                return;
            }
            cancelled = true;
            NovaScheduler.Cancellable current = delegate;
            delegate = null;
            capturedBindings = null;
            callback = null;
            if (current != null) {
                current.cancel();
            }
            if (registered) {
                scope.unregister(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized boolean isCancelled() {
            return cancelled || completed;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {
            cancel();
        }
    }
}
