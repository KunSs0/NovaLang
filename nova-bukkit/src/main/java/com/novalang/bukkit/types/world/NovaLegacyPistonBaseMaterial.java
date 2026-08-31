package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.PistonBaseMaterial;

/** Spigot 1.12.2 旧版活塞底座材料数据的 Fluxon 函数别名。 */
public final class NovaLegacyPistonBaseMaterial {

    private NovaLegacyPistonBaseMaterial() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PistonBaseMaterial.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE).invoke(arguments -> {
            piston(arguments).setFacingDirection(argument(arguments, 1, BlockFace.class));
            return null;
        }));
        builder.extension(PistonBaseMaterial.class, "facing", function -> function.returns(BlockFace.class)
                .invoke(arguments -> piston(arguments).getFacing()));
        builder.extension(PistonBaseMaterial.class, "isPowered", function -> function.returns(Boolean.class)
                .invoke(arguments -> piston(arguments).isPowered()));
        builder.extension(PistonBaseMaterial.class, "setPowered", function -> function.param("powered", Boolean.class).returns(Void.TYPE).invoke(arguments -> {
            piston(arguments).setPowered(argument(arguments, 1, Boolean.class));
            return null;
        }));
        builder.extension(PistonBaseMaterial.class, "isSticky", function -> function.returns(Boolean.class)
                .invoke(arguments -> piston(arguments).isSticky()));
        builder.extension(PistonBaseMaterial.class, "clone", function -> function.returns(PistonBaseMaterial.class)
                .invoke(arguments -> piston(arguments).clone()));
    }

    private static PistonBaseMaterial piston(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PistonBaseMaterial.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
