package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;

/** Spigot 1.12.2 BlockFace 的 Fluxon 函数别名。 */
final class NovaBlockFace {

    private NovaBlockFace() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(BlockFace.class, "modX", function -> function.returns(Integer.class)
                .invoke(arguments -> face(arguments).getModX()));
        builder.extension(BlockFace.class, "modY", function -> function.returns(Integer.class)
                .invoke(arguments -> face(arguments).getModY()));
        builder.extension(BlockFace.class, "modZ", function -> function.returns(Integer.class)
                .invoke(arguments -> face(arguments).getModZ()));
        builder.extension(BlockFace.class, "oppositeFace", function -> function.returns(BlockFace.class)
                .invoke(arguments -> face(arguments).getOppositeFace()));
    }

    private static BlockFace face(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockFace.class);
    }
}
