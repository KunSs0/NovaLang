package com.novalang.bukkit;

import com.novalang.runtime.NovaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.Executor;

/**
 * Bukkit/Spigot 环境下的 {@link NovaScheduler} 实现。
 *
 * <p>由 NovaLang 平台插件通过 {@link BukkitSchedulers#register(JavaPlugin)} 注册。</p>
 */
public final class BukkitNovaScheduler implements NovaScheduler {

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
        // Bukkit 以 tick 调度；不足一个 tick 的延迟也必须进入下一 tick。
        long ticks = Math.max(1, delayMs / 50);
        BukkitTask bt = Bukkit.getScheduler().runTaskLater(plugin, task, ticks);
        return new BukkitCancellable(bt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
        // 延迟和周期分别换算，保证 Bukkit 不接收到零 tick 周期。
        long delayTicks = Math.max(1, delayMs / 50);
        long periodTicks = Math.max(1, periodMs / 50);
        BukkitTask bt = Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
        return new BukkitCancellable(bt);
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
