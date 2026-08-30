package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.List;
import java.util.Random;

/** Spigot 1.12.2 ChunkGenerator 及其 BiomeGrid 扩展。 */
final class NovaGenerator {

    private NovaGenerator() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableLocation = JavaTypeRef.javaType(Location.class).nullable();
        builder.extension(ChunkGenerator.class, "generateChunkData", f -> f.param("world", World.class).param("random", Random.class).param("chunkX", Integer.class).param("chunkZ", Integer.class).param("biomeGrid", ChunkGenerator.BiomeGrid.class).returns(ChunkGenerator.ChunkData.class).invoke(a -> generator(a).generateChunkData(arg(a, 1, World.class), arg(a, 2, Random.class), arg(a, 3, Integer.class), arg(a, 4, Integer.class), arg(a, 5, ChunkGenerator.BiomeGrid.class))));
        builder.extension(ChunkGenerator.class, "canSpawn", f -> f.param("world", World.class).param("x", Integer.class).param("z", Integer.class).returns(Boolean.class).invoke(a -> generator(a).canSpawn(arg(a, 1, World.class), arg(a, 2, Integer.class), arg(a, 3, Integer.class))));
        builder.extension(ChunkGenerator.class, "getDefaultPopulators", f -> f.param("world", World.class).returns(JavaTypeRef.listOf(JavaTypeRef.javaType(BlockPopulator.class))).invoke(a -> generator(a).getDefaultPopulators(arg(a, 1, World.class))));
        builder.extension(ChunkGenerator.class, "getFixedSpawnLocation", f -> f.param("world", World.class).param("random", Random.class).returns(nullableLocation).invoke(a -> generator(a).getFixedSpawnLocation(arg(a, 1, World.class), arg(a, 2, Random.class))));
        builder.extension(ChunkGenerator.BiomeGrid.class, "getBiome", f -> f.param("x", Integer.class).param("z", Integer.class).returns(Biome.class).invoke(a -> biomeGrid(a).getBiome(arg(a, 1, Integer.class), arg(a, 2, Integer.class))));
        builder.extension(ChunkGenerator.BiomeGrid.class, "setBiome", f -> f.param("x", Integer.class).param("z", Integer.class).param("biome", Biome.class).invoke(a -> { biomeGrid(a).setBiome(arg(a, 1, Integer.class), arg(a, 2, Integer.class), arg(a, 3, Biome.class)); return null; }));
    }

    private static ChunkGenerator generator(Object[] a) { return NovaTypeSupport.argument(a, 0, ChunkGenerator.class); }
    private static ChunkGenerator.BiomeGrid biomeGrid(Object[] a) { return NovaTypeSupport.argument(a, 0, ChunkGenerator.BiomeGrid.class); }
    private static <T> T arg(Object[] a, int index, Class<T> type) { return NovaTypeSupport.argument(a, index, type); }
}
