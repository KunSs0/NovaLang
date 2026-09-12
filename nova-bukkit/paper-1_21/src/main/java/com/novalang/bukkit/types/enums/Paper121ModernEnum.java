package com.novalang.bukkit.types.enums;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Art;
import org.bukkit.block.Biome;

import java.util.Locale;

/** Paper 1.21.x 中由 {@code OldEnum} 接口表示的 Bukkit 类型注册器。 */
public final class Paper121ModernEnum {

    private Paper121ModernEnum() {
    }

    /** 注册 Paper 1.21.x 的艺术和生物群系查询函数。 */
    public static void register(JavaTypes.Builder builder) {
        registerArt(builder);
        registerBiome(builder);
    }

    /** 注册艺术查询函数并保留旧版名称规范化行为。 */
    private static void registerArt(JavaTypes.Builder builder) {
        builder.globalFunction("art", function -> function
                .param("name", String.class)
                .returns(JavaTypeRef.javaType(Art.class).nullable())
                .invoke1(String.class, value -> findArt(value)));
    }

    /** 注册生物群系查询函数并保留旧版名称规范化行为。 */
    private static void registerBiome(JavaTypes.Builder builder) {
        builder.globalFunction("biome", function -> function
                .param("name", String.class)
                .returns(JavaTypeRef.javaType(Biome.class).nullable())
                .invoke1(String.class, value -> findBiome(value)));
    }

    /** 按 Bukkit 枚举名称查找艺术，未找到时返回空值。 */
    private static Art findArt(String value) {
        try {
            return Art.valueOf(normalize(value));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /** 按 Bukkit 枚举名称查找生物群系，未找到时返回空值。 */
    private static Biome findBiome(String value) {
        try {
            return Biome.valueOf(normalize(value));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /** 将脚本名称转换为 Bukkit 常量名称。 */
    private static String normalize(String value) {
        return value.trim().replace(' ', '_').replace('.', '_').toUpperCase(Locale.ROOT);
    }
}
