package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Directional;

/** Spigot 1.12.2 旧版方向材料接口的 Fluxon 函数别名。 */
public final class NovaLegacyDirectional {

    private NovaLegacyDirectional() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Directional.class, "facing", function -> function.returns(BlockFace.class)
                .invoke(arguments -> directional(arguments).getFacing()));
        builder.extension(Directional.class, "setFacing", function -> function.param("face", BlockFace.class).returns(Void.TYPE).invoke(arguments -> {
            directional(arguments).setFacingDirection(argument(arguments, 1, BlockFace.class));
            return null;
        }));
    }

    private static Directional directional(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Directional.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
