package com.novalang.bukkit.paper.inventory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/** Paper 1.21 可选库存 API 的反射辅助。 */
public final class PaperInventoryReflection {

    private PaperInventoryReflection() {
    }

    /** 解析 Paper API 类型。 */
    public static Class<?> type(Class<?> owner, String name) {
        try {
            return Class.forName(name, false, owner.getClassLoader());
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("已通过 Requires 校验的 Bukkit 类不存在: " + name, exception);
        }
    }

    /** 解析 Paper API 方法。 */
    public static Method method(Class<?> type, String name, Class<?>... parameters) {
        try {
            return type.getMethod(name, parameters);
        } catch (NoSuchMethodException exception) {
            throw new IllegalStateException("已通过 Requires 校验的 Bukkit 方法不存在: " + type.getName() + '#' + name, exception);
        }
    }

    /** 调用 Paper API 方法并保留运行时异常语义。 */
    public static Object invoke(Method method, Object target, Object... parameters) {
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
}
