package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.DyeColor;
import org.bukkit.material.Colorable;

/** 旧版 Colorable 材料状态的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Colorable"})
final class NovaLegacyColorable {

    private NovaLegacyColorable() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Colorable.class, "color", function -> function
                .returns(DyeColor.class)
                .invoke(arguments -> colorable(arguments).getColor()));
        builder.extension(Colorable.class, "setColor", function -> function
                .param("color", DyeColor.class)
                .invoke(arguments -> {
                    colorable(arguments).setColor(NovaTypeSupport.argument(arguments, 1, DyeColor.class));
                    return null;
                }));
    }

    private static Colorable colorable(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Colorable.class);
    }
}
