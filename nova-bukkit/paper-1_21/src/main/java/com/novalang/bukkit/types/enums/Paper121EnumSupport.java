package com.novalang.bukkit.types.enums;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.util.Locale;
import java.util.function.Function;

/** Paper 1.21.x 枚举与 {@code OldEnum} 类型的注册辅助工具。 */
final class Paper121EnumSupport {

    private Paper121EnumSupport() {
    }

    /** 注册 Java 枚举类型的全局查询函数。 */
    static <E extends Enum<E>> void registerEnum(JavaTypes.Builder builder,
                                                  String functionName,
                                                  Class<E> enumClass) {
        builder.globalFunction(functionName, function -> function
                .param("name", String.class)
                .returns(JavaTypeRef.javaType(enumClass).nullable())
                .invoke1(String.class, value -> findEnum(enumClass, value)));
    }

    /** 注册 Paper {@code OldEnum} 类型的全局查询函数。 */
    static <T> void registerOldEnum(JavaTypes.Builder builder,
                                    String functionName,
                                    Class<T> type,
                                    Function<String, T> resolver) {
        builder.globalFunction(functionName, function -> function
                .param("name", String.class)
                .returns(JavaTypeRef.javaType(type).nullable())
                .invoke1(String.class, value -> findOldEnum(value, resolver)));
    }

    /** 通过标准 Java 枚举名称查询值，查询失败时返回空值。 */
    private static <E extends Enum<E>> E findEnum(Class<E> enumClass, String value) {
        try {
            return Enum.valueOf(enumClass, normalize(value));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /** 通过 Paper 类型的名称查询值，查询失败时返回空值。 */
    private static <T> T findOldEnum(String value, Function<String, T> resolver) {
        try {
            return resolver.apply(normalize(value));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /** 将脚本名称转换为 Bukkit 常量名称。 */
    private static String normalize(String value) {
        return value.trim().replace(' ', '_').replace('.', '_').toUpperCase(Locale.ROOT);
    }
}
