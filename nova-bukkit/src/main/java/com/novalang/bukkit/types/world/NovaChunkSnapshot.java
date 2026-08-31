package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;

/** Spigot 1.12.2 ChunkSnapshot 扩展；不注册 1.13+ BlockData API。 */
public final class NovaChunkSnapshot {

    private NovaChunkSnapshot() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef material = JavaTypeRef.javaType(Material.class).nullable();
        builder.extension(ChunkSnapshot.class, "x", f -> f.returns(Integer.class).invoke(a -> snap(a).getX()));
        builder.extension(ChunkSnapshot.class, "z", f -> f.returns(Integer.class).invoke(a -> snap(a).getZ()));
        builder.extension(ChunkSnapshot.class, "worldName", f -> f.returns(String.class).invoke(a -> snap(a).getWorldName()));
        builder.extension(ChunkSnapshot.class, "getBlockType", f -> f.param("x", Integer.class).param("y", Integer.class).param("z", Integer.class).returns(material).invoke(a -> snap(a).getBlockType(arg(a, 1, Integer.class), arg(a, 2, Integer.class), arg(a, 3, Integer.class))));
        builder.extension(ChunkSnapshot.class, "getData", f -> f.param("x", Integer.class).param("y", Integer.class).param("z", Integer.class).returns(Integer.class).invoke(a -> snap(a).getBlockData(arg(a, 1, Integer.class), arg(a, 2, Integer.class), arg(a, 3, Integer.class))));
        builder.extension(ChunkSnapshot.class, "getBlockSkyLight", f -> f.param("x", Integer.class).param("y", Integer.class).param("z", Integer.class).returns(Integer.class).invoke(a -> snap(a).getBlockSkyLight(arg(a, 1, Integer.class), arg(a, 2, Integer.class), arg(a, 3, Integer.class))));
        builder.extension(ChunkSnapshot.class, "getBlockEmittedLight", f -> f.param("x", Integer.class).param("y", Integer.class).param("z", Integer.class).returns(Integer.class).invoke(a -> snap(a).getBlockEmittedLight(arg(a, 1, Integer.class), arg(a, 2, Integer.class), arg(a, 3, Integer.class))));
        builder.extension(ChunkSnapshot.class, "getHighestBlockYAt", f -> f.param("x", Integer.class).param("z", Integer.class).returns(Integer.class).invoke(a -> snap(a).getHighestBlockYAt(arg(a, 1, Integer.class), arg(a, 2, Integer.class))));
        builder.extension(ChunkSnapshot.class, "getBiome", f -> f.param("x", Integer.class).param("z", Integer.class).returns(org.bukkit.block.Biome.class).invoke(a -> snap(a).getBiome(arg(a, 1, Integer.class), arg(a, 2, Integer.class))));
        builder.extension(ChunkSnapshot.class, "getRawBiomeTemperature", f -> f.param("x", Integer.class).param("z", Integer.class).returns(Double.class).invoke(a -> snap(a).getRawBiomeTemperature(arg(a, 1, Integer.class), arg(a, 2, Integer.class))));
        builder.extension(ChunkSnapshot.class, "captureFullTime", f -> f.returns(Long.class).invoke(a -> snap(a).getCaptureFullTime()));
        builder.extension(ChunkSnapshot.class, "isSectionEmpty", f -> f.param("section", Integer.class).returns(Boolean.class).invoke(a -> snap(a).isSectionEmpty(arg(a, 1, Integer.class))));
    }

    private static ChunkSnapshot snap(Object[] a) { return NovaTypeSupport.argument(a, 0, ChunkSnapshot.class); }
    private static <T> T arg(Object[] a, int index, Class<T> type) { return NovaTypeSupport.argument(a, index, type); }
}
