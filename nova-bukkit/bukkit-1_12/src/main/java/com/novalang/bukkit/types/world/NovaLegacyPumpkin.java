package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Pumpkin;

/** 旧版 Pumpkin 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Pumpkin"})
final class NovaLegacyPumpkin {

    private NovaLegacyPumpkin() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Pumpkin.class, "isLit", function -> function.returns(Boolean.class).invoke(arguments -> pumpkin(arguments).isLit()));
        builder.extension(Pumpkin.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { pumpkin(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Pumpkin.class, "facing", function -> function.returns(BlockFace.class).invoke(arguments -> pumpkin(arguments).getFacing()));
        builder.extension(Pumpkin.class, "toString", function -> function.returns(String.class).invoke(arguments -> pumpkin(arguments).toString()));
        builder.extension(Pumpkin.class, "clone", function -> function.returns(Pumpkin.class).invoke(arguments -> pumpkin(arguments).clone()));
    }

    private static Pumpkin pumpkin(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Pumpkin.class);
    }
}
