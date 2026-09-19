package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldInitEvent;

@Requires(classes = {"org.bukkit.event.world.WorldInitEvent"})
public final class NovaWorldInitEvent {
    private NovaWorldInitEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(WorldInitEvent.class, "handlers", f -> f.returns(HandlerList.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, WorldInitEvent.class).getHandlers()));
        builder.extension(WorldInitEvent.class, "handlerList", f -> f.returns(HandlerList.class)
                .invoke(a -> WorldInitEvent.getHandlerList()));
    }
}
