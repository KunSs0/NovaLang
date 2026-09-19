package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;

/** Spigot 1.12.2 BlockIterator 扩展。 */
final class NovaBlockIterator {

    private NovaBlockIterator() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(BlockIterator.class, "hasNext", f -> f.returns(Boolean.class).invoke(a -> iterator(a).hasNext()));
        builder.extension(BlockIterator.class, "next", f -> f.returns(Block.class).invoke(a -> iterator(a).next()));
        builder.extension(BlockIterator.class, "remove", f -> f.invoke(a -> { iterator(a).remove(); return null; }));
    }

    private static BlockIterator iterator(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockIterator.class);
    }
}
