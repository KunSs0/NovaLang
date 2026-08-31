package com.novalang.bukkit.types.server;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;

import java.io.File;
import java.util.regex.Pattern;

/** Spigot 1.12.2 PluginLoader 的 Fluxon 函数别名。 */
@Requires(classes = {"org.bukkit.plugin.PluginLoader"})
public final class NovaPluginLoader {

    private NovaPluginLoader() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PluginLoader.class, "loadPlugin", function -> function
                .param("file", File.class)
                .returns(JavaTypeRef.javaType(Plugin.class).nullable())
                .invoke(arguments -> loader(arguments).loadPlugin(
                        NovaTypeSupport.argument(arguments, 1, File.class))));
        builder.extension(PluginLoader.class, "getPluginDescription", function -> function
                .param("file", File.class)
                .returns(PluginDescriptionFile.class)
                .invoke(arguments -> loader(arguments).getPluginDescription(
                        NovaTypeSupport.argument(arguments, 1, File.class))));
        builder.extension(PluginLoader.class, "pluginFileFilters", function -> function
                .returns(Pattern[].class)
                .invoke(arguments -> loader(arguments).getPluginFileFilters()));
        builder.extension(PluginLoader.class, "enablePlugin", function -> function
                .param("plugin", Plugin.class)
                .invoke(arguments -> {
                    loader(arguments).enablePlugin(NovaTypeSupport.argument(arguments, 1, Plugin.class));
                    return null;
                }));
        builder.extension(PluginLoader.class, "disablePlugin", function -> function
                .param("plugin", Plugin.class)
                .invoke(arguments -> {
                    loader(arguments).disablePlugin(NovaTypeSupport.argument(arguments, 1, Plugin.class));
                    return null;
                }));
    }

    private static PluginLoader loader(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PluginLoader.class);
    }
}
