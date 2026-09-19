package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldLoadEvent;

@Requires(classes = {"org.bukkit.event.world.WorldLoadEvent"})
public final class NovaWorldLoadEvent {
    private NovaWorldLoadEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(WorldLoadEvent.class, "handlers", f -> f.returns(HandlerList.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, WorldLoadEvent.class).getHandlers()));
        builder.extension(WorldLoadEvent.class, "handlerList", f -> f.returns(HandlerList.class)
                .invoke(a -> WorldLoadEvent.getHandlerList()));
    }
}
