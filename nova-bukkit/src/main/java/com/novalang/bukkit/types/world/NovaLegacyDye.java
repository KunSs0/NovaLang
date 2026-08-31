package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.DyeColor;
import org.bukkit.material.Dye;

/** 旧版 Dye 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Dye"})
final class NovaLegacyDye {

    private NovaLegacyDye() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Dye.class, "color", function -> function.returns(DyeColor.class).invoke(arguments -> dye(arguments).getColor()));
        builder.extension(Dye.class, "setColor", function -> function.param("color", DyeColor.class).returns(Void.TYPE)
                .invoke(arguments -> { dye(arguments).setColor(NovaTypeSupport.argument(arguments, 1, DyeColor.class)); return null; }));
        builder.extension(Dye.class, "toString", function -> function.returns(String.class).invoke(arguments -> dye(arguments).toString()));
        builder.extension(Dye.class, "clone", function -> function.returns(Dye.class).invoke(arguments -> dye(arguments).clone()));
    }

    private static Dye dye(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Dye.class);
    }
}
