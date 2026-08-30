package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.ChunkPopulateEvent;

@Requires(classes = {"org.bukkit.event.world.ChunkPopulateEvent"})
public final class NovaChunkPopulateEvent {
    private NovaChunkPopulateEvent() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(ChunkPopulateEvent.class, "handlers", f -> f.returns(HandlerList.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, ChunkPopulateEvent.class).getHandlers()));
        builder.extension(ChunkPopulateEvent.class, "handlerList", f -> f.returns(HandlerList.class)
                .invoke(a -> ChunkPopulateEvent.getHandlerList()));
    }
}
