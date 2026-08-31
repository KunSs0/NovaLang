package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Lever;

/** Spigot 1.12.2 旧版拉杆材料数据的 Fluxon 函数别名。 */
public final class NovaLegacyLever {

    private NovaLegacyLever() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Lever.class, "isPowered", function -> function.returns(Boolean.class)
                .invoke(arguments -> lever(arguments).isPowered()));
        builder.extension(Lever.class, "setPowered", function -> function.param("powered", Boolean.class).returns(Void.TYPE).invoke(arguments -> {
            lever(arguments).setPowered(argument(arguments, 1, Boolean.class));
            return null;
        }));
        builder.extension(Lever.class, "attachedFace", function -> function.returns(BlockFace.class)
                .invoke(arguments -> lever(arguments).getAttachedFace()));
        builder.extension(Lever.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE).invoke(arguments -> {
            lever(arguments).setFacingDirection(argument(arguments, 1, BlockFace.class));
            return null;
        }));
        builder.extension(Lever.class, "toString", function -> function.returns(String.class)
                .invoke(arguments -> lever(arguments).toString()));
        builder.extension(Lever.class, "clone", function -> function.returns(Lever.class)
                .invoke(arguments -> lever(arguments).clone()));
    }

    private static Lever lever(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Lever.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
