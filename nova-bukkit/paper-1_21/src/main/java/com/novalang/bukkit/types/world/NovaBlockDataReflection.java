package com.novalang.bukkit.types.world;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/** 可选 BlockData 子接口注册器共享的反射调用工具。 */
final class NovaBlockDataReflection {

    private NovaBlockDataReflection() {
    }

    static Class<?> type(Class<?> owner, String name) {
        try {
            return Class.forName(name, false, owner.getClassLoader());
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("已通过 Requires 校验的 Bukkit 类不存在: " + name, exception);
        }
    }

    static Method method(Class<?> targetType, String name, Class<?>... parameterTypes) {
        try {
            return targetType.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException exception) {
            throw new IllegalStateException("已通过 Requires 校验的 Bukkit 方法不存在: " + targetType.getName() + '#' + name, exception);
        }
    }

    static Object invoke(Method method, Object target, Object... parameters) {
        try {
            return method.invoke(target, parameters);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("无法调用 Bukkit 方法: " + method, exception);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new IllegalStateException("Bukkit 方法执行失败: " + method, cause);
        }
    }

    static Object enumValue(Class<?> enumType, String value) {
        if (value == null || !enumType.isEnum()) {
            return null;
        }
        String normalized = value.trim().replace(' ', '_').replace('.', '_').toUpperCase(Locale.ROOT);
        Object[] constants = enumType.getEnumConstants();
        for (Object constant : constants) {
            if (((Enum<?>) constant).name().equals(normalized)) {
                return constant;
            }
        }
        return null;
    }
}
