package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

/** Spigot 1.12.2 BlockState 扩展。 */
final class NovaBlockState {

    private NovaBlockState() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableLocation = JavaTypeRef.javaType(Location.class).nullable();
        builder.extension(BlockState.class, "block", f -> f.returns(Block.class).invoke(a -> state(a).getBlock()));
        builder.extension(BlockState.class, "data", f -> f.returns(MaterialData.class).invoke(a -> state(a).getData()));
        builder.extension(BlockState.class, "type", f -> f.returns(Material.class).invoke(a -> state(a).getType()));
        builder.extension(BlockState.class, "lightLevel", f -> f.returns(Integer.class).invoke(a -> (int) state(a).getLightLevel()));
        builder.extension(BlockState.class, "world", f -> f.returns(World.class).invoke(a -> state(a).getWorld()));
        builder.extension(BlockState.class, "x", f -> f.returns(Integer.class).invoke(a -> state(a).getX()));
        builder.extension(BlockState.class, "y", f -> f.returns(Integer.class).invoke(a -> state(a).getY()));
        builder.extension(BlockState.class, "z", f -> f.returns(Integer.class).invoke(a -> state(a).getZ()));
        builder.extension(BlockState.class, "location", f -> f.returns(Location.class).invoke(a -> state(a).getLocation()));
        builder.extension(BlockState.class, "getLocation", f -> f.param("location", Location.class).returns(nullableLocation).invoke(a -> state(a).getLocation(arg(a, 1, Location.class))));
        builder.extension(BlockState.class, "chunk", f -> f.returns(Chunk.class).invoke(a -> state(a).getChunk()));
        builder.extension(BlockState.class, "setData", f -> f.param("data", MaterialData.class).invoke(a -> { state(a).setData(arg(a, 1, MaterialData.class)); return null; }));
        builder.extension(BlockState.class, "setType", f -> f.param("type", Material.class).invoke(a -> { state(a).setType(arg(a, 1, Material.class)); return null; }));
        builder.extension(BlockState.class, "update", f -> f.returns(Boolean.class).invoke(a -> state(a).update()));
        builder.extension(BlockState.class, "update", f -> f.param("force", Boolean.class).returns(Boolean.class).invoke(a -> state(a).update(arg(a, 1, Boolean.class))));
        builder.extension(BlockState.class, "update", f -> f.param("force", Boolean.class).param("applyPhysics", Boolean.class).returns(Boolean.class).invoke(a -> state(a).update(arg(a, 1, Boolean.class), arg(a, 2, Boolean.class))));
        builder.extension(BlockState.class, "rawData", f -> f.returns(Integer.class).invoke(a -> (int) state(a).getRawData()));
        builder.extension(BlockState.class, "setRawData", f -> f.param("data", Integer.class).invoke(a -> { state(a).setRawData(arg(a, 1, Integer.class).byteValue()); return null; }));
        builder.extension(BlockState.class, "isPlaced", f -> f.returns(Boolean.class).invoke(a -> state(a).isPlaced()));
    }

    private static BlockState state(Object[] a) { return NovaTypeSupport.argument(a, 0, BlockState.class); }
    private static <T> T arg(Object[] a, int index, Class<T> type) { return NovaTypeSupport.argument(a, index, type); }
}
