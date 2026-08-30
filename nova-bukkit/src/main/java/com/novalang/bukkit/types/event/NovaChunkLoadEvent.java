package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.ChunkLoadEvent;

@Requires(classes = {"org.bukkit.event.world.ChunkLoadEvent"})
public final class NovaChunkLoadEvent {
    private NovaChunkLoadEvent() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(ChunkLoadEvent.class, "isNewChunk", f -> f.returns(Boolean.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, ChunkLoadEvent.class).isNewChunk()));
        builder.extension(ChunkLoadEvent.class, "handlers", f -> f.returns(HandlerList.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, ChunkLoadEvent.class).getHandlers()));
        builder.extension(ChunkLoadEvent.class, "handlerList", f -> f.returns(HandlerList.class)
                .invoke(a -> ChunkLoadEvent.getHandlerList()));
    }
}
