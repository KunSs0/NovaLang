package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.ChunkUnloadEvent;

@Requires(classes = {"org.bukkit.event.world.ChunkUnloadEvent"})
public final class NovaChunkUnloadEvent {
    private NovaChunkUnloadEvent() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(ChunkUnloadEvent.class, "isSaveChunk", f -> f.returns(Boolean.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, ChunkUnloadEvent.class).isSaveChunk()));
        builder.extension(ChunkUnloadEvent.class, "setSaveChunk", f -> f.param("save", Boolean.class).returns(Void.TYPE)
                .invoke(a -> { NovaTypeSupport.argument(a, 0, ChunkUnloadEvent.class).setSaveChunk(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
        builder.extension(ChunkUnloadEvent.class, "handlers", f -> f.returns(HandlerList.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, ChunkUnloadEvent.class).getHandlers()));
        builder.extension(ChunkUnloadEvent.class, "handlerList", f -> f.returns(HandlerList.class)
                .invoke(a -> ChunkUnloadEvent.getHandlerList()));
    }
}
