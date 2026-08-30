package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

/** Bukkit 常用值对象构造和转换入口。 */
final class NovaValueFactory {

    private NovaValueFactory() {
    }

    static void register(JavaTypes.Builder builder) {
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
        builder.globalFunction("sound", function -> function
                .param("name", String.class)
                .returns(Sound.class)
                .invoke1(String.class, NovaValueFactory::requireSound));
        builder.globalFunction("soundOrNull", function -> function
                .param("name", String.class)
                .returns(JavaTypeRef.javaType(Sound.class).nullable())
                .invoke1(String.class, NovaValueFactory::findSound));
    }

    private static Color parseColor(String value) {
        String normalized = value.startsWith("#") ? value.substring(1) : value;
        return Color.fromRGB(Integer.parseInt(normalized, 16));
    }

    private static Sound requireSound(String value) {
        Sound sound = findSound(value);
        if (sound == null) {
            throw new IllegalArgumentException("音效不存在: " + value);
        }
        return sound;
    }

    private static Sound findSound(String value) {
        return NovaTypeSupport.findEnum(Sound.class, value);
    }
}
