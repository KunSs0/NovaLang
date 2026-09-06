package com.novalang.runtime;

/**
 * 当前脚本调用可选的调度任务所有者。
 *
 * <p>宿主运行时通过 {@link NovaScheduler} 提供实际调度能力；Workspace 等上层运行时
 * 可在当前调用期间安装本上下文，为原生 {@code schedule}/{@code scheduleRepeat} 增加
 * 资源归属和生命周期管理，而不会改变脚本层语法。</p>
 */
public interface NovaScheduleContext {

    /** 在目标线程安装捕获的宿主绑定；关闭句柄时恢复目标线程原上下文。 */
    AutoCloseable enter();

    /** 将异步任务登记到捕获的所有者；关闭登记只解除归属，不取消任务。 */
    AutoCloseable registerTask(NovaScheduler.Cancellable task);

    /**
     * 创建一次性任务。
     *
     * @param delayMs 延迟毫秒数
     * @param task 回调任务
     * @return 可取消句柄
     */
    NovaScheduler.Cancellable scheduleLater(long delayMs, Runnable task);

    /**
     * 创建循环任务。
     *
     * @param delayMs 首次延迟毫秒数
     * @param periodMs 循环周期毫秒数
     * @param task 回调任务
     * @return 可取消句柄
     */
    NovaScheduler.Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task);
}
