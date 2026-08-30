package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;

/** Spigot 1.12.2 World 扩展别名。 */
final class NovaWorldExtra {

    private NovaWorldExtra() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableLocation = JavaTypeRef.javaType(Location.class).nullable();
        builder.extension(World.class, "worldFolder", f -> f.returns(java.io.File.class).invoke(a -> world(a).getWorldFolder()));
        builder.extension(World.class, "worldType", f -> f.returns(WorldType.class).invoke(a -> world(a).getWorldType()));
        builder.extension(World.class, "environment", f -> f.returns(World.Environment.class).invoke(a -> world(a).getEnvironment()));
        builder.extension(World.class, "difficulty", f -> f.returns(Difficulty.class).invoke(a -> world(a).getDifficulty()));
        builder.extension(World.class, "setDifficulty", f -> f.param("difficulty", Difficulty.class).invoke(a -> { world(a).setDifficulty(arg(a, 1, Difficulty.class)); return null; }));
        builder.extension(World.class, "pvp", f -> f.returns(Boolean.class).invoke(a -> world(a).getPVP()));
        builder.extension(World.class, "setPVP", f -> f.param("pvp", Boolean.class).invoke(a -> { world(a).setPVP(arg(a, 1, Boolean.class)); return null; }));
        builder.extension(World.class, "spawnLocation", f -> f.returns(Location.class).invoke(a -> world(a).getSpawnLocation()));
        builder.extension(World.class, "setSpawnLocation", f -> f.param("x", Integer.class).param("y", Integer.class).param("z", Integer.class).returns(Boolean.class).invoke(a -> world(a).setSpawnLocation(arg(a, 1, Integer.class), arg(a, 2, Integer.class), arg(a, 3, Integer.class))));
        builder.extension(World.class, "time", f -> f.returns(Long.class).invoke(a -> world(a).getTime()));
        builder.extension(World.class, "setTime", f -> f.param("time", Long.class).invoke(a -> { world(a).setTime(arg(a, 1, Long.class)); return null; }));
        builder.extension(World.class, "fullTime", f -> f.returns(Long.class).invoke(a -> world(a).getFullTime()));
        builder.extension(World.class, "setFullTime", f -> f.param("time", Long.class).invoke(a -> { world(a).setFullTime(arg(a, 1, Long.class)); return null; }));
        builder.extension(World.class, "hasStorm", f -> f.returns(Boolean.class).invoke(a -> world(a).hasStorm()));
        builder.extension(World.class, "setStorm", f -> f.param("storm", Boolean.class).invoke(a -> { world(a).setStorm(arg(a, 1, Boolean.class)); return null; }));
        builder.extension(World.class, "weatherDuration", f -> f.returns(Integer.class).invoke(a -> world(a).getWeatherDuration()));
        builder.extension(World.class, "setWeatherDuration", f -> f.param("duration", Integer.class).invoke(a -> { world(a).setWeatherDuration(arg(a, 1, Integer.class)); return null; }));
        builder.extension(World.class, "isThundering", f -> f.returns(Boolean.class).invoke(a -> world(a).isThundering()));
        builder.extension(World.class, "setThundering", f -> f.param("thundering", Boolean.class).invoke(a -> { world(a).setThundering(arg(a, 1, Boolean.class)); return null; }));
        builder.extension(World.class, "thunderDuration", f -> f.returns(Integer.class).invoke(a -> world(a).getThunderDuration()));
        builder.extension(World.class, "setThunderDuration", f -> f.param("duration", Integer.class).invoke(a -> { world(a).setThunderDuration(arg(a, 1, Integer.class)); return null; }));
        builder.extension(World.class, "isAutoSave", f -> f.returns(Boolean.class).invoke(a -> world(a).isAutoSave()));
        builder.extension(World.class, "setAutoSave", f -> f.param("autosave", Boolean.class).invoke(a -> { world(a).setAutoSave(arg(a, 1, Boolean.class)); return null; }));
        builder.extension(World.class, "keepSpawnInMemory", f -> f.returns(Boolean.class).invoke(a -> world(a).getKeepSpawnInMemory()));
        builder.extension(World.class, "setKeepSpawnInMemory", f -> f.param("keep", Boolean.class).invoke(a -> { world(a).setKeepSpawnInMemory(arg(a, 1, Boolean.class)); return null; }));
        builder.extension(World.class, "worldBorder", f -> f.returns(WorldBorder.class).invoke(a -> world(a).getWorldBorder()));
        builder.extension(World.class, "getGameRuleValue", f -> f.param("rule", String.class).returns(JavaTypeRef.javaType(String.class).nullable()).invoke(a -> world(a).getGameRuleValue(arg(a, 1, String.class))));
        builder.extension(World.class, "setGameRuleValue", f -> f.param("rule", String.class).param("value", String.class).returns(Boolean.class).invoke(a -> world(a).setGameRuleValue(arg(a, 1, String.class), arg(a, 2, String.class))));
        builder.extension(World.class, "gameRules", f -> f.returns(JavaTypeRef.javaType(String[].class)).invoke(a -> world(a).getGameRules()));
        builder.extension(World.class, "getBiome", f -> f.param("x", Integer.class).param("z", Integer.class).returns(Biome.class).invoke(a -> world(a).getBiome(arg(a, 1, Integer.class), arg(a, 2, Integer.class))));
        builder.extension(World.class, "seaLevel", f -> f.returns(Integer.class).invoke(a -> world(a).getSeaLevel()));
        builder.extension(World.class, "getBlockAt", f -> f.param("location", Location.class).returns(org.bukkit.block.Block.class).invoke(a -> world(a).getBlockAt(arg(a, 1, Location.class))));
        builder.extension(World.class, "getBlockAt", f -> f.param("x", Integer.class).param("y", Integer.class).param("z", Integer.class).returns(org.bukkit.block.Block.class).invoke(a -> world(a).getBlockAt(arg(a, 1, Integer.class), arg(a, 2, Integer.class), arg(a, 3, Integer.class))));
        builder.extension(World.class, "getHighestBlockAt", f -> f.param("x", Integer.class).param("z", Integer.class).returns(org.bukkit.block.Block.class).invoke(a -> world(a).getHighestBlockAt(arg(a, 1, Integer.class), arg(a, 2, Integer.class))));
        builder.extension(World.class, "getHighestBlockAt", f -> f.param("location", Location.class).returns(org.bukkit.block.Block.class).invoke(a -> world(a).getHighestBlockAt(arg(a, 1, Location.class))));
        builder.extension(World.class, "getChunkAt", f -> f.param("location", Location.class).returns(org.bukkit.Chunk.class).invoke(a -> world(a).getChunkAt(arg(a, 1, Location.class))));
        builder.extension(World.class, "getChunkAt", f -> f.param("block", org.bukkit.block.Block.class).returns(org.bukkit.Chunk.class).invoke(a -> world(a).getChunkAt(arg(a, 1, org.bukkit.block.Block.class))));
        builder.extension(World.class, "getChunkAt", f -> f.param("x", Integer.class).param("z", Integer.class).returns(org.bukkit.Chunk.class).invoke(a -> world(a).getChunkAt(arg(a, 1, Integer.class), arg(a, 2, Integer.class))));
        builder.extension(World.class, "isChunkLoaded", f -> f.param("x", Integer.class).param("z", Integer.class).returns(Boolean.class).invoke(a -> world(a).isChunkLoaded(arg(a, 1, Integer.class), arg(a, 2, Integer.class))));
        builder.extension(World.class, "isChunkLoaded", f -> f.param("chunk", org.bukkit.Chunk.class).returns(Boolean.class).invoke(a -> world(a).isChunkLoaded(arg(a, 1, org.bukkit.Chunk.class))));
        builder.extension(World.class, "loadedChunks", f -> f.returns(JavaTypeRef.javaType(org.bukkit.Chunk[].class)).invoke(a -> world(a).getLoadedChunks()));
        builder.extension(World.class, "isChunkInUse", f -> f.param("x", Integer.class).param("z", Integer.class).returns(Boolean.class).invoke(a -> world(a).isChunkInUse(arg(a, 1, Integer.class), arg(a, 2, Integer.class))));
        builder.extension(World.class, "loadChunk", f -> f.param("chunk", org.bukkit.Chunk.class).invoke(a -> { world(a).loadChunk(arg(a, 1, org.bukkit.Chunk.class)); return null; }));
        builder.extension(World.class, "loadChunk", f -> f.param("x", Integer.class).param("z", Integer.class).invoke(a -> { world(a).loadChunk(arg(a, 1, Integer.class), arg(a, 2, Integer.class)); return null; }));
        builder.extension(World.class, "loadChunk", f -> f.param("x", Integer.class).param("z", Integer.class).param("generate", Boolean.class).returns(Boolean.class).invoke(a -> world(a).loadChunk(arg(a, 1, Integer.class), arg(a, 2, Integer.class), arg(a, 3, Boolean.class))));
        builder.extension(World.class, "unloadChunk", f -> f.param("chunk", org.bukkit.Chunk.class).returns(Boolean.class).invoke(a -> world(a).unloadChunk(arg(a, 1, org.bukkit.Chunk.class))));
        builder.extension(World.class, "unloadChunk", f -> f.param("x", Integer.class).param("z", Integer.class).returns(Boolean.class).invoke(a -> world(a).unloadChunk(arg(a, 1, Integer.class), arg(a, 2, Integer.class))));
        builder.extension(World.class, "unloadChunk", f -> f.param("x", Integer.class).param("z", Integer.class).param("save", Boolean.class).returns(Boolean.class).invoke(a -> world(a).unloadChunk(arg(a, 1, Integer.class), arg(a, 2, Integer.class), arg(a, 3, Boolean.class))));
        builder.extension(World.class, "unloadChunk", f -> f.param("x", Integer.class).param("z", Integer.class).param("save", Boolean.class).param("safe", Boolean.class).returns(Boolean.class).invoke(a -> world(a).unloadChunk(arg(a, 1, Integer.class), arg(a, 2, Integer.class), arg(a, 3, Boolean.class), arg(a, 4, Boolean.class))));
        builder.extension(World.class, "unloadChunkRequest", f -> f.param("x", Integer.class).param("z", Integer.class).returns(Boolean.class).invoke(a -> world(a).unloadChunkRequest(arg(a, 1, Integer.class), arg(a, 2, Integer.class))));
        builder.extension(World.class, "unloadChunkRequest", f -> f.param("x", Integer.class).param("z", Integer.class).param("safe", Boolean.class).returns(Boolean.class).invoke(a -> world(a).unloadChunkRequest(arg(a, 1, Integer.class), arg(a, 2, Integer.class), arg(a, 3, Boolean.class))));
        builder.extension(World.class, "refreshChunk", f -> f.param("x", Integer.class).param("z", Integer.class).returns(Boolean.class).invoke(a -> world(a).refreshChunk(arg(a, 1, Integer.class), arg(a, 2, Integer.class))));
        builder.extension(World.class, "regenerateChunk", f -> f.param("x", Integer.class).param("z", Integer.class).returns(Boolean.class).invoke(a -> world(a).regenerateChunk(arg(a, 1, Integer.class), arg(a, 2, Integer.class))));
    }

    private static World world(Object[] a) { return NovaTypeSupport.argument(a, 0, World.class); }
    private static <T> T arg(Object[] a, int index, Class<T> type) { return NovaTypeSupport.argument(a, index, type); }
}
