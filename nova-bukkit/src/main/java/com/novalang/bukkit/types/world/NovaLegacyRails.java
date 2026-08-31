package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Rails;

/** Spigot 1.12.2 旧版铁轨材料数据的 Fluxon 函数别名。 */
public final class NovaLegacyRails {

    private NovaLegacyRails() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Rails.class, "isOnSlope", function -> function.returns(Boolean.class)
                .invoke(arguments -> rails(arguments).isOnSlope()));
        builder.extension(Rails.class, "isCurve", function -> function.returns(Boolean.class)
                .invoke(arguments -> rails(arguments).isCurve()));
        builder.extension(Rails.class, "direction", function -> function.returns(BlockFace.class)
                .invoke(arguments -> rails(arguments).getDirection()));
        builder.extension(Rails.class, "setDirection", function -> function.param("face", BlockFace.class).param("slope", Boolean.class).returns(Void.TYPE).invoke(arguments -> {
            rails(arguments).setDirection(argument(arguments, 1, BlockFace.class), argument(arguments, 2, Boolean.class));
            return null;
        }));
        builder.extension(Rails.class, "toString", function -> function.returns(String.class)
                .invoke(arguments -> rails(arguments).toString()));
        builder.extension(Rails.class, "clone", function -> function.returns(Rails.class)
                .invoke(arguments -> rails(arguments).clone()));
    }

    private static Rails rails(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Rails.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
