package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.plugin.Plugin;

/** 插件启停事件共用的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.server.PluginEvent"})
public final class NovaPluginEvent {

    private NovaPluginEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PluginEvent.class, "plugin", function -> function
                .returns(Plugin.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, PluginEvent.class).getPlugin()));
    }
}
