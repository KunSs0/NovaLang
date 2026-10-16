package com.novalang.runtime;

import java.util.concurrent.CancellationException;

/**
 * 普通 Nova 程序的任务所有者，复用程序已有的回调生命周期。
 * <p>执行器仍由宿主提供；关闭 CompiledNova 会取消任务，不需要额外的运行容器。</p>
 */
final class ScriptScheduleContext implements NovaScheduleContext {
    private final ScriptCallbackContext owner;
    private final ClassLoader scriptClassLoader;

    /** 保存程序资源所有者及异步线程需要恢复的脚本类加载环境。 */
    ScriptScheduleContext(ScriptCallbackContext owner, ClassLoader scriptClassLoader) {
        this.owner = owner;
        this.scriptClassLoader = scriptClassLoader;
    }

    /** 在目标线程恢复任务所有者，关闭时按逆序恢复原上下文。 */
    @Override
    public AutoCloseable enter() {
        if (!owner.isValid()) {
            throw new CancellationException("Script task owner is closed");
        }
        ScriptCallbackContexts.ContextHandle callbacks = ScriptCallbackContexts.install(owner);
        NovaScheduleContexts.ContextHandle schedules = NovaScheduleContexts.install(this);
        ClassLoader previousLoader = com.novalang.runtime.interpreter.JavaInterop.getScriptClassLoader();
        com.novalang.runtime.interpreter.JavaInterop.setScriptClassLoader(scriptClassLoader);
        return () -> {
            com.novalang.runtime.interpreter.JavaInterop.setScriptClassLoader(previousLoader);
            schedules.close();
            callbacks.close();
        };
    }

    /** 将异步任务的取消操作登记到程序，返回仅解除登记的句柄。 */
    @Override
    public AutoCloseable registerTask(NovaScheduler.Cancellable task) {
        try {
            return owner.register(task::cancel);
        } catch (RuntimeException failure) {
            task.cancel();
            throw failure;
        }
    }

    /** 通过明确安装的宿主调度器创建一次性任务。 */
    @Override
    public NovaScheduler.Cancellable scheduleLater(long delayMs, Runnable task) {
        return schedule(delayMs, 0L, task, false);
    }

    /** 通过明确安装的宿主调度器创建循环任务。 */
    @Override
    public NovaScheduler.Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
        return schedule(delayMs, periodMs, task, true);
    }

    /** 先登记任务所有权，再提交给宿主，覆盖立即执行和关闭竞态。 */
    private NovaScheduler.Cancellable schedule(long delayMs, long periodMs, Runnable action, boolean repeating) {
        NovaScheduler scheduler = SchedulerHolder.get();
        if (scheduler == null) {
            throw new IllegalStateException("The Nova scheduler is not installed");
        }
        ScheduledTask task = new ScheduledTask(action, repeating);
        task.bindRegistration(registerTask(task));
        try {
            NovaScheduler.Cancellable delegate;
            if (repeating) {
                delegate = scheduler.scheduleRepeat(delayMs, periodMs, task::execute);
            } else {
                delegate = scheduler.scheduleLater(delayMs, task::execute);
            }
            task.bind(delegate);
            return task;
        } catch (RuntimeException failure) {
            task.cancel();
            throw failure;
        }
    }

    /** 在宿主任务句柄和程序资源之间维护可幂等释放的登记。 */
    private final class ScheduledTask implements NovaScheduler.Cancellable {
        private Runnable action;
        private final boolean repeating;
        private NovaScheduler.Cancellable delegate;
        private AutoCloseable registration;
        private boolean cancelled;

        /** 保存本次任务回调及完成策略。 */
        private ScheduledTask(Runnable action, boolean repeating) {
            this.action = action;
            this.repeating = repeating;
        }

        /** 如果程序在登记返回前已关闭，立即解除刚创建的登记。 */
        private synchronized void bindRegistration(AutoCloseable handle) {
            registration = handle;
            if (cancelled) {
                detach();
            }
        }

        /** 宿主可以同步执行任务，绑定时处理已经完成或取消的情况。 */
        private synchronized void bind(NovaScheduler.Cancellable scheduled) {
            if (cancelled) {
                scheduled.cancel();
            } else {
                delegate = scheduled;
            }
        }

        /** 恢复程序上下文后执行，单次任务结束即解除资源登记。 */
        private void execute() {
            Runnable callback;
            synchronized (this) {
                if (cancelled || !owner.isValid()) {
                    return;
                }
                callback = action;
            }
            try (AutoCloseable installed = enter()) {
                callback.run();
            } catch (RuntimeException failure) {
                throw failure;
            } catch (Exception failure) {
                throw new IllegalStateException("Cannot restore script task context", failure);
            } finally {
                if (!repeating) {
                    cancel();
                }
            }
        }

        /** 取消实际任务并解除资源登记；重复调用不产生额外操作。 */
        @Override
        public synchronized void cancel() {
            if (cancelled) {
                return;
            }
            cancelled = true;
            action = null;
            if (delegate != null) {
                delegate.cancel();
                delegate = null;
            }
            if (registration != null) {
                detach();
            }
        }

        /** 解除任务对程序资源表的引用，不再次取消底层任务。 */
        private void detach() {
            try {
                registration.close();
            } catch (Exception failure) {
                throw new IllegalStateException("Cannot detach script task", failure);
            } finally {
                registration = null;
            }
        }

        /** @return 任务是否已经取消或执行完毕 */
        @Override
        public synchronized boolean isCancelled() {
            return cancelled;
        }
    }
}
