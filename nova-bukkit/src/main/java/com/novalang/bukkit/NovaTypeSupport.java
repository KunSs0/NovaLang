package com.novalang.bukkit;

import com.novalang.runtime.NovaValueConversions;

import java.util.Locale;

/** Bukkit JavaTypes 注册器共享的参数和枚举转换工具。 */
final class NovaTypeSupport {

    private NovaTypeSupport() {
    }

    static <T> T argument(Object[] arguments, int index, Class<T> targetType) {
        Object value = index < arguments.length ? arguments[index] : null;
        return NovaValueConversions.convertArg(value, targetType);
    }

    static <E extends Enum<E>> E findEnum(Class<E> enumClass, String value) {
        try {
            return Enum.valueOf(enumClass, normalizeEnumName(value));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private static String normalizeEnumName(String value) {
        return value.trim().replace(' ', '_').replace('.', '_').toUpperCase(Locale.ROOT);
    }
}
