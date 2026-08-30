package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

/** Spigot 1.12.2 BlockPopulator 的明确调用契约。 */
@Requires(classes = {"org.bukkit.generator.BlockPopulator"})
public final class NovaBlockPopulator {
    private NovaBlockPopulator() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockPopulator.class, "populate", function -> function
                .param("world", World.class)
                .param("random", Random.class)
                .param("chunk", Chunk.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    populator(arguments).populate(
                            NovaTypeSupport.argument(arguments, 1, World.class),
                            NovaTypeSupport.argument(arguments, 2, Random.class),
                            NovaTypeSupport.argument(arguments, 3, Chunk.class));
                    return null;
                }));
    }

    private static BlockPopulator populator(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockPopulator.class);
    }
}
