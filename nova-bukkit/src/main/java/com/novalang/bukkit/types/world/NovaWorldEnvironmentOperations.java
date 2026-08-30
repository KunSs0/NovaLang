package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.material.MaterialData;

/** Spigot 1.12.2 World 的环境、生成与爆炸操作别名。 */
final class NovaWorldEnvironmentOperations {

    private NovaWorldEnvironmentOperations() {
    }

    static void register(JavaTypes.Builder builder) {
        registerExplosions(builder);
        builder.extension(World.class, "save", function -> function
                .invoke(arguments -> {
                    world(arguments).save();
                    return null;
                }));
        builder.extension(World.class, "populators", function -> function
                .returns(JavaTypeRef.listOf(JavaTypeRef.javaType(BlockPopulator.class)))
                .invoke(arguments -> world(arguments).getPopulators()));
        builder.extension(World.class, "spawnFallingBlock", function -> function
                .param("location", Location.class)
                .param("data", MaterialData.class)
                .returns(FallingBlock.class)
                .invoke(arguments -> world(arguments).spawnFallingBlock(
                        argument(arguments, 1, Location.class),
                        argument(arguments, 2, MaterialData.class))));
        builder.extension(World.class, "spawnFallingBlock", function -> function
                .param("location", Location.class)
                .param("material", Material.class)
                .param("data", Integer.class)
                .returns(FallingBlock.class)
                .invoke(arguments -> world(arguments).spawnFallingBlock(
                        argument(arguments, 1, Location.class),
                        argument(arguments, 2, Material.class),
                        argument(arguments, 3, Integer.class).byteValue())));
        builder.extension(World.class, "spawnFallingBlock", function -> function
                .param("location", Location.class)
                .param("blockId", Integer.class)
                .param("data", Integer.class)
                .returns(FallingBlock.class)
                .invoke(arguments -> world(arguments).spawnFallingBlock(
                        argument(arguments, 1, Location.class),
                        argument(arguments, 2, Integer.class),
                        argument(arguments, 3, Integer.class).byteValue())));
        builder.extension(World.class, "setSpawnFlags", function -> function
                .param("allowMonsters", Boolean.class)
                .param("allowAnimals", Boolean.class)
                .invoke(arguments -> {
                    world(arguments).setSpawnFlags(
                            argument(arguments, 1, Boolean.class),
                            argument(arguments, 2, Boolean.class));
                    return null;
                }));
        builder.extension(World.class, "allowAnimals", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> world(arguments).getAllowAnimals()));
        builder.extension(World.class, "allowMonsters", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> world(arguments).getAllowMonsters()));
        builder.extension(World.class, "canGenerateStructures", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> world(arguments).canGenerateStructures()));
        builder.extension(World.class, "ticksPerAnimalSpawns", function -> function
                .returns(Long.class)
                .invoke(arguments -> world(arguments).getTicksPerAnimalSpawns()));
        builder.extension(World.class, "setTicksPerAnimalSpawns", function -> function
                .param("ticks", Integer.class)
                .invoke(arguments -> {
                    world(arguments).setTicksPerAnimalSpawns(
                            argument(arguments, 1, Integer.class));
                    return null;
                }));
        builder.extension(World.class, "ticksPerMonsterSpawns", function -> function
                .returns(Long.class)
                .invoke(arguments -> world(arguments).getTicksPerMonsterSpawns()));
        builder.extension(World.class, "setTicksPerMonsterSpawns", function -> function
                .param("ticks", Integer.class)
                .invoke(arguments -> {
                    world(arguments).setTicksPerMonsterSpawns(
                            argument(arguments, 1, Integer.class));
                    return null;
                }));
        builder.extension(World.class, "isGameRule", function -> function
                .param("rule", String.class)
                .returns(Boolean.class)
                .invoke(arguments -> world(arguments).isGameRule(
                        argument(arguments, 1, String.class))));
    }

    private static void registerExplosions(JavaTypes.Builder builder) {
        builder.extension(World.class, "createExplosion", function -> function
                .param("location", Location.class)
                .param("power", Float.class)
                .returns(Boolean.class)
                .invoke(arguments -> world(arguments).createExplosion(
                        argument(arguments, 1, Location.class),
                        argument(arguments, 2, Float.class))));
        builder.extension(World.class, "createExplosion", function -> function
                .param("location", Location.class)
                .param("power", Float.class)
                .param("setFire", Boolean.class)
                .returns(Boolean.class)
                .invoke(arguments -> world(arguments).createExplosion(
                        argument(arguments, 1, Location.class),
                        argument(arguments, 2, Float.class),
                        argument(arguments, 3, Boolean.class))));
        builder.extension(World.class, "createExplosion", function -> function
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .param("power", Float.class)
                .returns(Boolean.class)
                .invoke(arguments -> world(arguments).createExplosion(
                        argument(arguments, 1, Double.class),
                        argument(arguments, 2, Double.class),
                        argument(arguments, 3, Double.class),
                        argument(arguments, 4, Float.class))));
        builder.extension(World.class, "createExplosion", function -> function
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .param("power", Float.class)
                .param("setFire", Boolean.class)
                .returns(Boolean.class)
                .invoke(arguments -> world(arguments).createExplosion(
                        argument(arguments, 1, Double.class),
                        argument(arguments, 2, Double.class),
                        argument(arguments, 3, Double.class),
                        argument(arguments, 4, Float.class),
                        argument(arguments, 5, Boolean.class))));
        builder.extension(World.class, "createExplosion", function -> function
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .param("power", Float.class)
                .param("setFire", Boolean.class)
                .param("breakBlocks", Boolean.class)
                .returns(Boolean.class)
                .invoke(arguments -> world(arguments).createExplosion(
                        argument(arguments, 1, Double.class),
                        argument(arguments, 2, Double.class),
                        argument(arguments, 3, Double.class),
                        argument(arguments, 4, Float.class),
                        argument(arguments, 5, Boolean.class),
                        argument(arguments, 6, Boolean.class))));
    }

    private static World world(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, World.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
