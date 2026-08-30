package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

/** Spigot 1.12.2 Chunk 扩展。 */
final class NovaChunk {

    private NovaChunk() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableSnapshot = JavaTypeRef.javaType(ChunkSnapshot.class).nullable();
        builder.extension(Chunk.class, "x", f -> f.returns(Integer.class).invoke(a -> chunk(a).getX()));
        builder.extension(Chunk.class, "z", f -> f.returns(Integer.class).invoke(a -> chunk(a).getZ()));
        builder.extension(Chunk.class, "world", f -> f.returns(World.class).invoke(a -> chunk(a).getWorld()));
        builder.extension(Chunk.class, "getBlock", f -> f.param("x", Integer.class).param("y", Integer.class).param("z", Integer.class).returns(Block.class).invoke(a -> chunk(a).getBlock(arg(a, 1, Integer.class), arg(a, 2, Integer.class), arg(a, 3, Integer.class))));
        builder.extension(Chunk.class, "chunkSnapshot", f -> f.returns(nullableSnapshot).invoke(a -> chunk(a).getChunkSnapshot()));
        builder.extension(Chunk.class, "getChunkSnapshot", f -> f.param("includeMaxBlockY", Boolean.class).param("includeBiome", Boolean.class).param("includeBiomeTemp", Boolean.class).returns(nullableSnapshot).invoke(a -> chunk(a).getChunkSnapshot(arg(a, 1, Boolean.class), arg(a, 2, Boolean.class), arg(a, 3, Boolean.class))));
        builder.extension(Chunk.class, "entities", f -> f.returns(JavaTypeRef.javaType(Entity[].class)).invoke(a -> chunk(a).getEntities()));
        builder.extension(Chunk.class, "tileEntities", f -> f.returns(JavaTypeRef.javaType(BlockState[].class)).invoke(a -> chunk(a).getTileEntities()));
        builder.extension(Chunk.class, "isLoaded", f -> f.returns(Boolean.class).invoke(a -> chunk(a).isLoaded()));
        builder.extension(Chunk.class, "load", f -> f.returns(Boolean.class).invoke(a -> chunk(a).load()));
        builder.extension(Chunk.class, "load", f -> f.param("generate", Boolean.class).returns(Boolean.class).invoke(a -> chunk(a).load(arg(a, 1, Boolean.class))));
        builder.extension(Chunk.class, "unload", f -> f.returns(Boolean.class).invoke(a -> chunk(a).unload()));
        builder.extension(Chunk.class, "unload", f -> f.param("save", Boolean.class).returns(Boolean.class).invoke(a -> chunk(a).unload(arg(a, 1, Boolean.class))));
        builder.extension(Chunk.class, "unload", f -> f.param("save", Boolean.class).param("safe", Boolean.class).returns(Boolean.class).invoke(a -> chunk(a).unload(arg(a, 1, Boolean.class), arg(a, 2, Boolean.class))));
        builder.extension(Chunk.class, "isSlimeChunk", f -> f.returns(Boolean.class).invoke(a -> chunk(a).isSlimeChunk()));
    }

    private static Chunk chunk(Object[] a) { return NovaTypeSupport.argument(a, 0, Chunk.class); }
    private static <T> T arg(Object[] a, int index, Class<T> type) { return NovaTypeSupport.argument(a, index, type); }
}
