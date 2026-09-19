package com.novalang.bukkit;

import com.novalang.runtime.SchedulerHolder;
import com.novalang.runtime.interpreter.Interpreter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bukkit 调度器注册入口。
 *
 * <pre>{@code
 * // 在平台插件加载阶段注册唯一调度器
 * BukkitSchedulers.register(this);
 * }</pre>
 */
public final class BukkitSchedulers {

    /**
     * 禁止实例化静态工具类。
     */
    private BukkitSchedulers() {
    }

    /**
     * 创建并全局注册 Bukkit 调度器。
     *
     * @param plugin Bukkit 插件实例
     * @return 已注册的调度器
     * @throws IllegalArgumentException 插件实例为空时抛出
     * @throws IllegalStateException 已存在全局调度器时抛出
     */
    public static com.novalang.runtime.NovaScheduler register(JavaPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("The Bukkit plugin must not be null.");
        }

        com.novalang.runtime.NovaScheduler registered = SchedulerHolder.get();
        if (registered != null) {
            throw new IllegalStateException("A Nova scheduler has already been registered.");
        }

        // 调度器必须在业务 Workspace 加载前完成唯一注册。
        com.novalang.runtime.NovaScheduler scheduler = create(plugin);
        SchedulerHolder.set(scheduler);
        return scheduler;
    }

    /**
     * 注销调用方持有的全局 Bukkit 调度器。
     *
     * @param scheduler 调用方在注册阶段获得的调度器
     * @throws IllegalArgumentException 调度器为空时抛出
     * @throws IllegalStateException 当前全局调度器不属于调用方时抛出
     */
    public static void unregister(com.novalang.runtime.NovaScheduler scheduler) {
        if (scheduler == null) {
            throw new IllegalArgumentException("The Nova scheduler must not be null.");
        }

        com.novalang.runtime.NovaScheduler registered = SchedulerHolder.get();
        if (registered != scheduler) {
            throw new IllegalStateException("The registered Nova scheduler is not owned by this plugin.");
        }

        // 仅所有者可以释放全局调度器，并清除编译路径保存的主线程执行器引用。
        Interpreter.resetGlobalSchedulerState();
    }

    /**
     * 仅创建调度器实例，不全局注册。
     *
     * @param plugin Bukkit 插件实例
     * @return 调度器实例
     * @throws IllegalArgumentException 插件实例为空时抛出
     */
    public static com.novalang.runtime.NovaScheduler create(JavaPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("The Bukkit plugin must not be null.");
        }
        return new BukkitNovaScheduler(plugin);
    }
}
