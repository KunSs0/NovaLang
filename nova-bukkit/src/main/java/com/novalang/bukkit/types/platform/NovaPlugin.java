package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import java.io.File;
import java.io.InputStream;
import java.util.Set;
import java.util.logging.Logger;

/** Plugin、PluginManager 和 Bukkit Services 的 Fluxon 别名。 */
final class NovaPlugin {

    private NovaPlugin() {
    }

    static void register(JavaTypes.Builder b) {
        b.extension(Plugin.class, "name", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Plugin.class).getName()));
        b.extension(Plugin.class, "server", f -> f.returns(Server.class).invoke(a -> NovaTypeSupport.argument(a, 0, Plugin.class).getServer()));
        b.extension(Plugin.class, "description", f -> f.returns(PluginDescriptionFile.class).invoke(a -> NovaTypeSupport.argument(a, 0, Plugin.class).getDescription()));
        b.extension(Plugin.class, "dataFolder", f -> f.returns(File.class).invoke(a -> NovaTypeSupport.argument(a, 0, Plugin.class).getDataFolder()));
        b.extension(Plugin.class, "config", f -> f.returns(FileConfiguration.class).invoke(a -> NovaTypeSupport.argument(a, 0, Plugin.class).getConfig()));
        b.extension(Plugin.class, "pluginLoader", f -> f.returns(PluginLoader.class).invoke(a -> NovaTypeSupport.argument(a, 0, Plugin.class).getPluginLoader()));
        b.extension(Plugin.class, "logger", f -> f.returns(Logger.class).invoke(a -> NovaTypeSupport.argument(a, 0, Plugin.class).getLogger()));
        b.extension(Plugin.class, "getResource", f -> f.param("filename", String.class).returns(JavaTypeRef.javaType(InputStream.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Plugin.class).getResource(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(Plugin.class, "isEnabled", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Plugin.class).isEnabled()));
        b.extension(Plugin.class, "isNaggable", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Plugin.class).isNaggable()));
        b.extension(Plugin.class, "setNaggable", f -> f.param("canNag", Boolean.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Plugin.class).setNaggable(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        b.extension(Plugin.class, "saveConfig", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, Plugin.class).saveConfig(); return null; }));
        b.extension(Plugin.class, "saveDefaultConfig", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, Plugin.class).saveDefaultConfig(); return null; }));
        b.extension(Plugin.class, "reloadConfig", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, Plugin.class).reloadConfig(); return null; }));
        b.extension(Plugin.class, "saveResource", f -> f.param("resourcePath", String.class).param("replace", Boolean.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Plugin.class).saveResource(NovaTypeSupport.argument(a, 1, String.class), NovaTypeSupport.argument(a, 2, Boolean.class)); return null; }));

        b.extension(PluginManager.class, "getPlugin", f -> f.param("name", String.class).returns(JavaTypeRef.javaType(Plugin.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, PluginManager.class).getPlugin(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(PluginManager.class, "plugins", f -> f.returns(Plugin[].class).invoke(a -> NovaTypeSupport.argument(a, 0, PluginManager.class).getPlugins()));
        b.extension(PluginManager.class, "isPluginEnabled", f -> f.param("name", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, PluginManager.class).isPluginEnabled(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(PluginManager.class, "isPluginEnabled", f -> f.param("plugin", Plugin.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, PluginManager.class).isPluginEnabled(NovaTypeSupport.argument(a, 1, Plugin.class))));
        b.extension(PluginManager.class, "enablePlugin", f -> f.param("plugin", Plugin.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).enablePlugin(NovaTypeSupport.argument(a, 1, Plugin.class)); return null; }));
        b.extension(PluginManager.class, "disablePlugin", f -> f.param("plugin", Plugin.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).disablePlugin(NovaTypeSupport.argument(a, 1, Plugin.class)); return null; }));
        b.extension(PluginManager.class, "disablePlugins", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).disablePlugins(); return null; }));
        b.extension(PluginManager.class, "clearPlugins", f -> f.invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).clearPlugins(); return null; }));
        b.extension(PluginManager.class, "callEvent", f -> f.param("event", Event.class).invoke(a -> { NovaTypeSupport.argument(a, 0, PluginManager.class).callEvent(NovaTypeSupport.argument(a, 1, Event.class)); return null; }));
        b.extension(PluginManager.class, "getPermission", f -> f.param("name", String.class).returns(JavaTypeRef.javaType(Permission.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, PluginManager.class).getPermission(NovaTypeSupport.argument(a, 1, String.class))));

        b.extension(RegisteredServiceProvider.class, "service", f -> f.returns(Class.class).invoke(a -> NovaTypeSupport.argument(a, 0, RegisteredServiceProvider.class).getService()));
        b.extension(RegisteredServiceProvider.class, "provider", f -> f.returns(Object.class).invoke(a -> NovaTypeSupport.argument(a, 0, RegisteredServiceProvider.class).getProvider()));
        b.extension(RegisteredServiceProvider.class, "plugin", f -> f.returns(Plugin.class).invoke(a -> NovaTypeSupport.argument(a, 0, RegisteredServiceProvider.class).getPlugin()));
        b.extension(RegisteredServiceProvider.class, "priority", f -> f.returns(ServicePriority.class).invoke(a -> NovaTypeSupport.argument(a, 0, RegisteredServiceProvider.class).getPriority()));
        b.extension(ServicesManager.class, "unregisterAll", f -> f.param("plugin", Plugin.class).invoke(a -> { NovaTypeSupport.argument(a, 0, ServicesManager.class).unregisterAll(NovaTypeSupport.argument(a, 1, Plugin.class)); return null; }));
        b.extension(ServicesManager.class, "unregister", f -> f.param("provider", Object.class).invoke(a -> { NovaTypeSupport.argument(a, 0, ServicesManager.class).unregister(NovaTypeSupport.argument(a, 1, Object.class)); return null; }));
    }
}
