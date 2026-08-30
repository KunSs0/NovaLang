package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldSaveEvent;

@Requires(classes = {"org.bukkit.event.world.WorldSaveEvent"})
public final class NovaWorldSaveEvent {
    private NovaWorldSaveEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(WorldSaveEvent.class, "handlers", f -> f.returns(HandlerList.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, WorldSaveEvent.class).getHandlers()));
        builder.extension(WorldSaveEvent.class, "handlerList", f -> f.returns(HandlerList.class)
                .invoke(a -> WorldSaveEvent.getHandlerList()));
    }
}
