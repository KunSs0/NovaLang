package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/** Paper 1.21.x 的 Bukkit BlockFace 增量 API。 */
public final class NovaBlockFaceModern {

    private NovaBlockFaceModern() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockFace.class, "direction", function -> function
                .returns(Vector.class)
                .invoke(arguments -> face(arguments).getDirection()));
        builder.extension(BlockFace.class, "isCartesian", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> face(arguments).isCartesian()));
    }

    private static BlockFace face(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockFace.class);
    }

}
