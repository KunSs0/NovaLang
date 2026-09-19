package com.novalang.bukkit.types.value;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;
import org.bukkit.DyeColor;

/** Spigot 1.12.2 DyeColor 的 Fluxon 值对象别名。 */
public final class NovaDyeColor {

    private NovaDyeColor() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(DyeColor.class, "woolData", f -> f.returns(Byte.class).invoke(a -> color(a).getWoolData()));
        builder.extension(DyeColor.class, "dyeData", f -> f.returns(Byte.class).invoke(a -> color(a).getDyeData()));
        builder.extension(DyeColor.class, "color", f -> f.returns(Color.class).invoke(a -> color(a).getColor()));
        builder.extension(DyeColor.class, "fireworkColor", f -> f.returns(Color.class).invoke(a -> color(a).getFireworkColor()));
    }

    private static DyeColor color(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, DyeColor.class);
    }
}
