package com.novalang.runtime;

/**
 * 进程级唯一 Nova 调度器持有者。
 * 编译路径和解释器路径都通过此静态持有者获取宿主安装的同一调度器。
 */
public final class SchedulerHolder {

    private static volatile NovaScheduler instance;

    /**
     * 禁止实例化静态持有者。
     */
    private SchedulerHolder() {
    }

    /**
     * 获取当前进程级调度器。
     *
     * @return 已安装的调度器；尚未安装时返回 {@code null}
     */
    public static NovaScheduler get() {
        return instance;
    }

    /**
     * 安装进程级唯一调度器。
     *
     * <p>同一实例可以由 Workspace 重复确认；不同实例不得覆盖当前宿主调度器。</p>
     *
     * @param scheduler 宿主调度器
     * @throws IllegalArgumentException 调度器为空时抛出
     * @throws IllegalStateException 已安装其他调度器实例时抛出
     */
    public static synchronized void set(NovaScheduler scheduler) {
        if (scheduler == null) {
            throw new IllegalArgumentException("The Nova scheduler must not be null.");
        }
        if (instance != null && instance != scheduler) {
            throw new IllegalStateException("A different Nova scheduler has already been installed.");
        }
        instance = scheduler;
    }

    /**
     * 清理进程级调度器状态。
     *
     * <p>生产环境只能由平台宿主在卸载阶段调用；测试在每个用例后调用。</p>
     */
    public static synchronized void clear() {
        instance = null;
    }
}
