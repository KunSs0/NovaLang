package com.novalang.bukkit.types.server;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

/** Spigot 1.12.2 EventExecutor 的事件执行别名。 */
@Requires(classes = {"org.bukkit.plugin.EventExecutor"})
public final class NovaEventExecutor {

    private NovaEventExecutor() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EventExecutor.class, "execute", function -> function
                .param("listener", Listener.class)
                .param("event", Event.class)
                .invoke(arguments -> {
                    executor(arguments).execute(
                            NovaTypeSupport.argument(arguments, 1, Listener.class),
                            NovaTypeSupport.argument(arguments, 2, Event.class));
                    return null;
                }));
    }

    private static EventExecutor executor(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EventExecutor.class);
    }
}
