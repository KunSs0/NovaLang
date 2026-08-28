package com.novalang.runtime;

/**
 * Nova 原生调度函数的统一运行时入口。
 */
public final class NovaSchedules {

    /**
     * 工具类不允许实例化。
     */
    private NovaSchedules() {
    }

    /**
     * 延迟提交一次性任务。
     *
     * @param delayMs 延迟毫秒数
     * @param task 回调任务
     * @return 可取消句柄
     */
    public static NovaScheduler.Cancellable scheduleLater(long delayMs, Runnable task) {
        if (delayMs < 0L) {
            throw new NovaException(NovaException.ErrorKind.ARGUMENT_MISMATCH,
                    "schedule 的 delayMs 不能为负数");
        }
        requireTask(task);
        NovaScheduleContext context = NovaScheduleContexts.current();
        if (context != null) {
            return context.scheduleLater(delayMs, task);
        }
        return requireScheduler().scheduleLater(delayMs, task);
    }

    /**
     * 提交循环任务。
     *
     * @param delayMs 首次延迟毫秒数
     * @param periodMs 循环周期毫秒数
     * @param task 回调任务
     * @return 可取消句柄
     */
    public static NovaScheduler.Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
        if (delayMs < 0L) {
            throw new NovaException(NovaException.ErrorKind.ARGUMENT_MISMATCH,
                    "scheduleRepeat 的 delayMs 不能为负数");
        }
        if (periodMs <= 0L) {
            throw new NovaException(NovaException.ErrorKind.ARGUMENT_MISMATCH,
                    "scheduleRepeat 的 periodMs 必须大于零");
        }
        requireTask(task);
        NovaScheduleContext context = NovaScheduleContexts.current();
        if (context != null) {
            return context.scheduleRepeat(delayMs, periodMs, task);
        }
        return requireScheduler().scheduleRepeat(delayMs, periodMs, task);
    }

    /**
     * 获取已安装的宿主调度器。
     *
     * @return 当前宿主调度器
     */
    private static NovaScheduler requireScheduler() {
        NovaScheduler scheduler = SchedulerHolder.get();
        if (scheduler == null) {
            throw new NovaException(NovaException.ErrorKind.INTERNAL,
                    "未配置调度器", "请先调用 Nova.setScheduler()");
        }
        return scheduler;
    }

    /**
     * 校验回调任务不为空。
     *
     * @param task 待校验任务
     */
    private static void requireTask(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException("task must not be null");
        }
    }
}
