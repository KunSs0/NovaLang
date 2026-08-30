package com.novalang.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.Executor;

/**
 * Bukkit/Spigot 环境下的 {@link com.novalang.runtime.NovaScheduler} 实现。
 *
 * <p>由 NovaLang 平台插件通过 {@link BukkitSchedulers#register(JavaPlugin)} 注册。</p>
 */
public final class BukkitNovaScheduler implements com.novalang.runtime.NovaScheduler {

    private final JavaPlugin plugin;
    private final Executor mainExec;
    private final Executor asyncExec;

    /**
     * 创建绑定到指定 Bukkit 插件生命周期的调度器。
     *
     * @param plugin NovaLang Bukkit 插件实例
     */
    BukkitNovaScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.mainExec = task -> Bukkit.getScheduler().runTask(plugin, task);
        this.asyncExec = task -> Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Executor mainExecutor() {
        return mainExec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Executor asyncExecutor() {
        return asyncExec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMainThread() {
        return Bukkit.isPrimaryThread();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cancellable scheduleLater(long delayMs, Runnable task) {
        long ticks = millisToTicks(delayMs);
        BukkitTask bt = Bukkit.getScheduler().runTaskLater(plugin, task, ticks);
        return new BukkitCancellable(bt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
        long delayTicks = millisToTicks(delayMs);
        long periodTicks = millisToTicks(periodMs);
        BukkitTask bt = Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
        return new BukkitCancellable(bt);
    }

    /**
     * 将毫秒延迟向上取整为 Bukkit tick。
     *
     * <p>宿主无法在半 tick 时刻运行任务，因此非 50ms 整数倍的请求必须等待到下一个
     * tick，避免在请求的最短延迟之前提前触发。零毫秒仍统一进入下一 tick。</p>
     *
     * @param millis 延迟或周期毫秒数
     * @return 至少为一的 tick 数
     */
    private long millisToTicks(long millis) {
        long ticks = millis / 50L;
        if (millis % 50L != 0L) {
            ticks++;
        }
        return Math.max(1L, ticks);
    }

    /**
     * Bukkit 任务取消句柄。
     */
    private static final class BukkitCancellable implements Cancellable {
        private final BukkitTask task;

        /**
         * 包装 Bukkit 任务。
         *
         * @param task Bukkit 调度任务
         */
        BukkitCancellable(BukkitTask task) {
            this.task = task;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void cancel() {
            task.cancel();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCancelled() {
            return task.isCancelled();
        }
    }
}
