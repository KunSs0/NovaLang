package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.DyeColor;
import org.bukkit.material.Wool;

/** 旧版 Wool 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Wool"})
final class NovaLegacyWool {

    private NovaLegacyWool() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Wool.class, "color", function -> function.returns(DyeColor.class).invoke(arguments -> wool(arguments).getColor()));
        builder.extension(Wool.class, "setColor", function -> function.param("color", DyeColor.class).returns(Void.TYPE)
                .invoke(arguments -> { wool(arguments).setColor(NovaTypeSupport.argument(arguments, 1, DyeColor.class)); return null; }));
        builder.extension(Wool.class, "toString", function -> function.returns(String.class).invoke(arguments -> wool(arguments).toString()));
        builder.extension(Wool.class, "clone", function -> function.returns(Wool.class).invoke(arguments -> wool(arguments).clone()));
    }

    private static Wool wool(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Wool.class);
    }
}
