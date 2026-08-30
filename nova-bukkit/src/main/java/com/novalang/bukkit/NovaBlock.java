package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

/** Spigot 1.12.2 Block 扩展。 */
final class NovaBlock {

    private NovaBlock() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableLocation = JavaTypeRef.javaType(Location.class).nullable();
        builder.extension(Block.class, "type", f -> f.returns(Material.class).invoke(a -> block(a).getType()));
        builder.extension(Block.class, "setType", f -> f.param("type", Material.class).invoke(a -> { block(a).setType(arg(a, 1, Material.class)); return null; }));
        builder.extension(Block.class, "data", f -> f.returns(Integer.class).invoke(a -> (int) block(a).getData()));
        builder.extension(Block.class, "setData", f -> f.param("data", Integer.class).invoke(a -> { block(a).setData(arg(a, 1, Integer.class).byteValue()); return null; }));
        builder.extension(Block.class, "state", f -> f.returns(BlockState.class).invoke(a -> block(a).getState()));
        builder.extension(Block.class, "world", f -> f.returns(org.bukkit.World.class).invoke(a -> block(a).getWorld()));
        builder.extension(Block.class, "chunk", f -> f.returns(Chunk.class).invoke(a -> block(a).getChunk()));
        builder.extension(Block.class, "location", f -> f.returns(Location.class).invoke(a -> block(a).getLocation()));
        builder.extension(Block.class, "getLocation", f -> f.param("location", Location.class).returns(nullableLocation).invoke(a -> block(a).getLocation(arg(a, 1, Location.class))));
        builder.extension(Block.class, "x", f -> f.returns(Integer.class).invoke(a -> block(a).getX()));
        builder.extension(Block.class, "y", f -> f.returns(Integer.class).invoke(a -> block(a).getY()));
        builder.extension(Block.class, "z", f -> f.returns(Integer.class).invoke(a -> block(a).getZ()));
        builder.extension(Block.class, "biome", f -> f.returns(Biome.class).invoke(a -> block(a).getBiome()));
        builder.extension(Block.class, "setBiome", f -> f.param("biome", Biome.class).invoke(a -> { block(a).setBiome(arg(a, 1, Biome.class)); return null; }));
        builder.extension(Block.class, "lightLevel", f -> f.returns(Integer.class).invoke(a -> (int) block(a).getLightLevel()));
        builder.extension(Block.class, "lightFromSky", f -> f.returns(Integer.class).invoke(a -> (int) block(a).getLightFromSky()));
        builder.extension(Block.class, "lightFromBlocks", f -> f.returns(Integer.class).invoke(a -> (int) block(a).getLightFromBlocks()));
        builder.extension(Block.class, "isEmpty", f -> f.returns(Boolean.class).invoke(a -> block(a).isEmpty()));
        builder.extension(Block.class, "isLiquid", f -> f.returns(Boolean.class).invoke(a -> block(a).isLiquid()));
        builder.extension(Block.class, "temperature", f -> f.returns(Double.class).invoke(a -> block(a).getTemperature()));
        builder.extension(Block.class, "humidity", f -> f.returns(Double.class).invoke(a -> block(a).getHumidity()));
        builder.extension(Block.class, "pistonMoveReaction", f -> f.returns(PistonMoveReaction.class).invoke(a -> block(a).getPistonMoveReaction()));
        builder.extension(Block.class, "getRelative", f -> f.param("face", BlockFace.class).returns(Block.class).invoke(a -> block(a).getRelative(arg(a, 1, BlockFace.class))));
        builder.extension(Block.class, "getRelative", f -> f.param("face", BlockFace.class).param("distance", Integer.class).returns(Block.class).invoke(a -> block(a).getRelative(arg(a, 1, BlockFace.class), arg(a, 2, Integer.class))));
        builder.extension(Block.class, "getFace", f -> f.param("block", Block.class).returns(JavaTypeRef.javaType(BlockFace.class).nullable()).invoke(a -> block(a).getFace(arg(a, 1, Block.class))));
        builder.extension(Block.class, "breakNaturally", f -> f.returns(Boolean.class).invoke(a -> block(a).breakNaturally()));
        builder.extension(Block.class, "breakNaturally", f -> f.param("tool", ItemStack.class).returns(Boolean.class).invoke(a -> block(a).breakNaturally(arg(a, 1, ItemStack.class))));
        builder.extension(Block.class, "drops", f -> f.returns(JavaTypeRef.javaType(Collection.class)).invoke(a -> block(a).getDrops()));
        builder.extension(Block.class, "getDrops", f -> f.param("tool", ItemStack.class).returns(JavaTypeRef.javaType(Collection.class)).invoke(a -> block(a).getDrops(arg(a, 1, ItemStack.class))));
    }

    private static Block block(Object[] a) { return NovaTypeSupport.argument(a, 0, Block.class); }
    private static <T> T arg(Object[] a, int index, Class<T> type) { return NovaTypeSupport.argument(a, index, type); }
}
