package com.novalang.bukkit.types.server;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.Permissible;
import java.io.File;

/** Spigot 1.12.2 PluginManager 中未由其他类型注册的别名。 */
@Requires(classes = {
        "org.bukkit.plugin.PluginManager",
        "org.bukkit.plugin.EventExecutor"
})
@SuppressWarnings("unchecked")
final class NovaPluginManager {

    private NovaPluginManager() {
    }

    static void register(JavaTypes.Builder b) {
        b.extension(PluginManager.class, "registerInterface", f -> f.param("loader", Class.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).registerInterface(NovaTypeSupport.argument(a, 1, Class.class)); return null; }));
        b.extension(PluginManager.class, "loadPlugin", f -> f.param("file", File.class).returns(JavaTypeRef.javaType(Plugin.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, PluginManager.class).loadPlugin(NovaTypeSupport.argument(a, 1, File.class))));
        b.extension(PluginManager.class, "loadPlugins", f -> f.param("directory", File.class).returns(Plugin[].class).invoke(a -> NovaTypeSupport.argument(a, 0, PluginManager.class).loadPlugins(NovaTypeSupport.argument(a, 1, File.class))));
        b.extension(PluginManager.class, "registerEvents", f -> f.param("listener", Listener.class).param("plugin", Plugin.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).registerEvents(NovaTypeSupport.argument(a, 1, Listener.class), NovaTypeSupport.argument(a, 2, Plugin.class)); return null; }));
        b.extension(PluginManager.class, "registerEvent", f -> f.param("event", Class.class).param("listener", Listener.class).param("priority", EventPriority.class).param("executor", EventExecutor.class).param("plugin", Plugin.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).registerEvent(NovaTypeSupport.argument(a, 1, Class.class), NovaTypeSupport.argument(a, 2, Listener.class), NovaTypeSupport.argument(a, 3, EventPriority.class), NovaTypeSupport.argument(a, 4, EventExecutor.class), NovaTypeSupport.argument(a, 5, Plugin.class)); return null; }));
        b.extension(PluginManager.class, "registerEvent", f -> f.param("event", Class.class).param("listener", Listener.class).param("priority", EventPriority.class).param("executor", EventExecutor.class).param("plugin", Plugin.class).param("ignoreCancelled", Boolean.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).registerEvent(NovaTypeSupport.argument(a, 1, Class.class), NovaTypeSupport.argument(a, 2, Listener.class), NovaTypeSupport.argument(a, 3, EventPriority.class), NovaTypeSupport.argument(a, 4, EventExecutor.class), NovaTypeSupport.argument(a, 5, Plugin.class), NovaTypeSupport.argument(a, 6, Boolean.class)); return null; }));
        b.extension(PluginManager.class, "addPermission", f -> f.param("permission", Permission.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).addPermission(NovaTypeSupport.argument(a, 1, Permission.class)); return null; }));
        b.extension(PluginManager.class, "removePermission", f -> f.param("permission", Permission.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).removePermission(NovaTypeSupport.argument(a, 1, Permission.class)); return null; }));
        b.extension(PluginManager.class, "removePermission", f -> f.param("name", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).removePermission(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        b.extension(PluginManager.class, "getDefaultPermissions", f -> f.param("op", Boolean.class).returns(JavaTypeRef.javaType(java.util.Set.class)).invoke(a -> NovaTypeSupport.argument(a, 0, PluginManager.class).getDefaultPermissions(NovaTypeSupport.argument(a, 1, Boolean.class))));
        b.extension(PluginManager.class, "recalculatePermissionDefaults", f -> f.param("permission", Permission.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).recalculatePermissionDefaults(NovaTypeSupport.argument(a, 1, Permission.class)); return null; }));
        b.extension(PluginManager.class, "subscribeToPermission", f -> f.param("permission", String.class).param("permissible", Permissible.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).subscribeToPermission(NovaTypeSupport.argument(a, 1, String.class), NovaTypeSupport.argument(a, 2, Permissible.class)); return null; }));
        b.extension(PluginManager.class, "unsubscribeFromPermission", f -> f.param("permission", String.class).param("permissible", Permissible.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).unsubscribeFromPermission(NovaTypeSupport.argument(a, 1, String.class), NovaTypeSupport.argument(a, 2, Permissible.class)); return null; }));
        b.extension(PluginManager.class, "getPermissionSubscriptions", f -> f.param("permission", String.class).returns(JavaTypeRef.javaType(java.util.Set.class)).invoke(a -> NovaTypeSupport.argument(a, 0, PluginManager.class).getPermissionSubscriptions(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(PluginManager.class, "subscribeToDefaultPerms", f -> f.param("op", Boolean.class).param("permissible", Permissible.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).subscribeToDefaultPerms(NovaTypeSupport.argument(a, 1, Boolean.class), NovaTypeSupport.argument(a, 2, Permissible.class)); return null; }));
        b.extension(PluginManager.class, "unsubscribeFromDefaultPerms", f -> f.param("op", Boolean.class).param("permissible", Permissible.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).unsubscribeFromDefaultPerms(NovaTypeSupport.argument(a, 1, Boolean.class), NovaTypeSupport.argument(a, 2, Permissible.class)); return null; }));
        b.extension(PluginManager.class, "getDefaultPermSubscriptions", f -> f.param("op", Boolean.class).returns(JavaTypeRef.javaType(java.util.Set.class)).invoke(a -> NovaTypeSupport.argument(a, 0, PluginManager.class).getDefaultPermSubscriptions(NovaTypeSupport.argument(a, 1, Boolean.class))));
        b.extension(PluginManager.class, "getPermissions", f -> f.returns(JavaTypeRef.javaType(java.util.Set.class)).invoke(a -> NovaTypeSupport.argument(a, 0, PluginManager.class).getPermissions()));
        b.extension(PluginManager.class, "useTimings", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, PluginManager.class).useTimings()));
    }
}
