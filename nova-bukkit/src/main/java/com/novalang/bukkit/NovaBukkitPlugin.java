package com.novalang.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * NovaLang 在 Bukkit/Paper 服务端中的独立运行时插件。
 *
 * <p>插件在加载阶段安装进程级唯一调度器，使硬依赖 NovaLang 的业务插件可以在
 * 自身加载或启用阶段安全创建 Workspace。业务 Workspace 的创建、重载和销毁仍由
 * 对应业务插件负责。</p>
 */
public final class NovaBukkitPlugin extends JavaPlugin {

    private static volatile NovaBukkitPlugin instance;

    private com.novalang.runtime.NovaScheduler scheduler;

    /**
     * 在业务插件加载前注册全局 Bukkit 调度器。
     */
    @Override
    public void onLoad() {
        if (instance != null && instance != this) {
            throw new IllegalStateException("Another NovaLang Bukkit plugin instance is already loaded");
        }
        instance = this;
        // STARTUP 加载顺序配合业务插件硬依赖，保证 Workspace 启动前调度器已就绪。
        try {
            scheduler = BukkitSchedulers.register(this);
            getLogger().info("NovaLang runtime " + getDescription().getVersion() + " loaded.");
        } catch (RuntimeException exception) {
            instance = null;
            throw exception;
        }
    }

    @Override
    public void onEnable() {
        if (getCommand("nova") != null) {
            getCommand("nova").setExecutor(new NovaPluginCommand());
        }
    }

    /**
     * 在插件卸载时释放由当前插件拥有的全局调度器。
     */
    @Override
    public void onDisable() {
        if (scheduler != null) {
            // 服务端关闭时先撤销全局入口，业务 Workspace 应已由各自插件完成销毁。
            BukkitSchedulers.unregister(scheduler);
            scheduler = null;
        }
        instance = null;
        getLogger().info("NovaLang runtime unloaded.");
    }

    /**
     * 获取当前已加载的 NovaLang Bukkit 插件实例。
     *
     * @return 当前插件实例
     * @throws IllegalStateException NovaLang 尚未加载或已经卸载时抛出
     */
    static NovaBukkitPlugin requireInstance() {
        NovaBukkitPlugin current = instance;
        if (current == null) {
            throw new IllegalStateException("The NovaLang Bukkit plugin is not loaded");
        }
        return current;
    }
}
