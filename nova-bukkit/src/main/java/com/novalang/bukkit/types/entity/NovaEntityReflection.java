package com.novalang.bukkit.types.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

/** 可选实体 API 注册器的反射辅助。 */
final class NovaEntityReflection {
    private NovaEntityReflection() { }
    static Class<?> type(Class<?> owner, String name) {
        try {
            return Class.forName(name, false, owner.getClassLoader());
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("已通过 Requires 校验的 Bukkit 类不存在: " + name, exception);
        }
    }
    static Method method(Class<?> type, String name, Class<?>... parameters) {
        try {
            return type.getMethod(name, parameters);
        } catch (NoSuchMethodException exception) {
            throw new IllegalStateException("已通过 Requires 校验的 Bukkit 方法不存在: " + type.getName() + '#' + name, exception);
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    static Object enumValue(Class<?> type, String name) {
        try {
            return Enum.valueOf((Class<? extends Enum>) type.asSubclass(Enum.class), name);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    static void registerEnum(JavaTypes.Builder builder, String functionName, Class<?> enumType) {
        builder.globalFunction(functionName, function -> function
                .param("name", String.class)
                .returns(JavaTypeRef.javaType(enumType).nullable())
                .invoke(arguments -> enumValue(enumType, (String) arguments[0])));
    }
}
