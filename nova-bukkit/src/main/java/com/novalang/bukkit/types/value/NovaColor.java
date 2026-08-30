package com.novalang.bukkit.types.value;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;

/** Spigot 1.12.2 Color 的 Fluxon 值对象别名。 */
public final class NovaColor {

    private NovaColor() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Color.class, "fromRGB", f -> f.param("rgb", Integer.class).returns(Color.class).invoke(a -> Color.fromRGB(arg(a, 1))));
        builder.extension(Color.class, "fromRGB", f -> f.param("red", Integer.class).param("green", Integer.class).param("blue", Integer.class).returns(Color.class).invoke(a -> Color.fromRGB(arg(a, 1), arg(a, 2), arg(a, 3))));
        builder.extension(Color.class, "fromBGR", f -> f.param("bgr", Integer.class).returns(Color.class).invoke(a -> Color.fromBGR(arg(a, 1))));
        builder.extension(Color.class, "fromBGR", f -> f.param("blue", Integer.class).param("green", Integer.class).param("red", Integer.class).returns(Color.class).invoke(a -> Color.fromBGR(arg(a, 1), arg(a, 2), arg(a, 3))));
        builder.extension(Color.class, "red", f -> f.returns(Integer.class).invoke(a -> color(a).getRed()));
        builder.extension(Color.class, "setRed", f -> f.param("red", Integer.class).returns(Color.class).invoke(a -> color(a).setRed(arg(a, 1))));
        builder.extension(Color.class, "green", f -> f.returns(Integer.class).invoke(a -> color(a).getGreen()));
        builder.extension(Color.class, "setGreen", f -> f.param("green", Integer.class).returns(Color.class).invoke(a -> color(a).setGreen(arg(a, 1))));
        builder.extension(Color.class, "blue", f -> f.returns(Integer.class).invoke(a -> color(a).getBlue()));
        builder.extension(Color.class, "setBlue", f -> f.param("blue", Integer.class).returns(Color.class).invoke(a -> color(a).setBlue(arg(a, 1))));
        builder.extension(Color.class, "asRGB", f -> f.returns(Integer.class).invoke(a -> color(a).asRGB()));
        builder.extension(Color.class, "asBGR", f -> f.returns(Integer.class).invoke(a -> color(a).asBGR()));
        builder.extension(Color.class, "mixDyes", f -> f.returns(Color.class).invoke(a -> color(a).mixDyes()));
        builder.extension(Color.class, "mixColors", f -> f.returns(Color.class).invoke(a -> color(a).mixColors()));
        builder.extension(Color.class, "serialize", f -> f.returns(java.util.Map.class).invoke(a -> color(a).serialize()));
        builder.extension(Color.class, "toString", f -> f.returns(String.class).invoke(a -> color(a).toString()));
    }

    private static Color color(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Color.class);
    }

    private static Integer arg(Object[] arguments, int index) {
        return NovaTypeSupport.argument(arguments, index, Integer.class);
    }
}
