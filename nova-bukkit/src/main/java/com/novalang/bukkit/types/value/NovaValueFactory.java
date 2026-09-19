package com.novalang.bukkit.types.value;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.util.Vector;

/** Bukkit 常用值对象构造和转换入口。 */
public final class NovaValueFactory {

    private NovaValueFactory() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaColor.register(builder);
        NovaDyeColor.register(builder);
        NovaFireworkEffect.register(builder);
        NovaBukkitRegistrar.register(builder, NovaNamespacedKey.class, NovaNamespacedKey::register);
        NovaBukkitRegistrar.register(builder, NovaKeyed.class, NovaKeyed::register);
        NovaBukkitRegistrar.register(builder, NovaNameable.class, NovaNameable::register);
        builder.globalFunction("vector", function -> function
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .returns(Vector.class)
                .invoke3(Double.class, Double.class, Double.class, Vector::new));
        builder.globalFunction("color", function -> function
                .param("hex", String.class)
                .returns(Color.class)
                .invoke1(String.class, NovaValueFactory::parseColor));
        builder.globalFunction("color", function -> function
                .param("rgb", Integer.class)
                .returns(Color.class)
                .invoke1(Integer.class, Color::fromRGB));
        builder.globalFunction("color", function -> function
                .param("red", Integer.class)
                .param("green", Integer.class)
                .param("blue", Integer.class)
                .returns(Color.class)
                .invoke3(Integer.class, Integer.class, Integer.class, Color::fromRGB));
        builder.globalFunction("dyeColorByFireworkColor", function -> function
                .param("color", Color.class)
                .returns(JavaTypeRef.javaType(DyeColor.class).nullable())
                .invoke1(Color.class, DyeColor::getByFireworkColor));
        builder.globalFunction("dyeColorByColor", function -> function
                .param("color", Color.class)
                .returns(JavaTypeRef.javaType(DyeColor.class).nullable())
                .invoke1(Color.class, DyeColor::getByColor));
    }

    private static Color parseColor(String value) {
        String normalized = value.startsWith("#") ? value.substring(1) : value;
        return Color.fromRGB(Integer.parseInt(normalized, 16));
    }

}
