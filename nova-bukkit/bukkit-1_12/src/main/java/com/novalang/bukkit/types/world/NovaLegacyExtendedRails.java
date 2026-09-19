package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.ExtendedRails;

/** 旧版 ExtendedRails 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.ExtendedRails"})
final class NovaLegacyExtendedRails {

    private NovaLegacyExtendedRails() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(ExtendedRails.class, "isCurve", function -> function.returns(Boolean.class)
                .invoke(arguments -> rails(arguments).isCurve()));
        builder.extension(ExtendedRails.class, "setDirection", function -> function.param("face", BlockFace.class).param("onSlope", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { rails(arguments).setDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class), NovaTypeSupport.argument(arguments, 2, Boolean.class)); return null; }));
        builder.extension(ExtendedRails.class, "clone", function -> function.returns(ExtendedRails.class)
                .invoke(arguments -> rails(arguments).clone()));
    }

    private static ExtendedRails rails(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ExtendedRails.class);
    }
}
