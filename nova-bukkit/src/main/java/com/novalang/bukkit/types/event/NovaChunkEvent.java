package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Chunk;
import org.bukkit.event.world.ChunkEvent;

@Requires(classes = {"org.bukkit.event.world.ChunkEvent"})
public final class NovaChunkEvent {
    private NovaChunkEvent() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(ChunkEvent.class, "chunk", f -> f.returns(Chunk.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, ChunkEvent.class).getChunk()));
    }
}
