package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldUnloadEvent;

@Requires(classes = {"org.bukkit.event.world.WorldUnloadEvent"})
public final class NovaWorldUnloadEvent {
    private NovaWorldUnloadEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(WorldUnloadEvent.class, "isCancelled", f -> f.returns(Boolean.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, WorldUnloadEvent.class).isCancelled()));
        builder.extension(WorldUnloadEvent.class, "setCancelled", f -> f.param("cancelled", Boolean.class).returns(Void.TYPE)
                .invoke(a -> { NovaTypeSupport.argument(a, 0, WorldUnloadEvent.class).setCancelled(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(WorldUnloadEvent.class, "handlers", f -> f.returns(HandlerList.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, WorldUnloadEvent.class).getHandlers()));
        builder.extension(WorldUnloadEvent.class, "handlerList", f -> f.returns(HandlerList.class)
                .invoke(a -> WorldUnloadEvent.getHandlerList()));
    }
}
