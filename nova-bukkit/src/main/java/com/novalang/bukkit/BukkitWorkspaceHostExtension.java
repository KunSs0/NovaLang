package com.novalang.bukkit;

import com.novalang.runtime.Nova;
import com.novalang.workspace.WorkspaceHostExtension;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据 Workspace 宿主类加载器解析真实 Bukkit 插件，并统一安装 NoBukkit。
 */
final class BukkitWorkspaceHostExtension implements WorkspaceHostExtension {

    @Override
    public void install(Nova nova, ClassLoader scriptClassLoader) {
        if (scriptClassLoader == null) {
            throw new IllegalStateException("Workspace script ClassLoader is not available");
        }
        Plugin owner = resolveOwner(scriptClassLoader);
        NovaBukkit.install(nova, owner);
    }

    private Plugin resolveOwner(ClassLoader scriptClassLoader) {
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        List<Plugin> matches = new ArrayList<Plugin>();
        for (Plugin plugin : plugins) {
            ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();
            if (pluginClassLoader == scriptClassLoader) {
                matches.add(plugin);
            }
        }
        if (matches.size() == 1) {
            return matches.get(0);
        }
        if (matches.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot resolve Bukkit Plugin for Workspace script ClassLoader: "
                            + scriptClassLoader);
        }
        throw new IllegalStateException(
                "Multiple Bukkit Plugins use the Workspace script ClassLoader: " + matches);
    }
}
