package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypes;

/** Bukkit 类型注册器的可选依赖门禁。 */
public final class NovaBukkitRegistrar {

    private NovaBukkitRegistrar() {
    }

    public static void register(JavaTypes.Builder builder, Class<?> registrarType, Registrar registrar) {
        if (!isSatisfied(registrarType)) {
            return;
        }
        registrar.register(builder);
    }

    public static boolean isSatisfied(Class<?> registrarType) {
        Requires requires = registrarType.getAnnotation(Requires.class);
        if (requires == null) {
            return true;
        }
        ClassLoader classLoader = registrarType.getClassLoader();
        if (classLoader == null) {
            return false;
        }
        for (String className : requires.classes()) {
            if (!isPresent(classLoader, className)) {
                return false;
            }
        }
        for (String methodReference : requires.methods()) {
            if (!isMethodPresent(classLoader, methodReference)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPresent(ClassLoader classLoader, String className) {
        try {
            Class.forName(className, false, classLoader);
            return true;
        } catch (ClassNotFoundException exception) {
            return false;
        } catch (LinkageError error) {
            return false;
        }
    }

    private static boolean isMethodPresent(ClassLoader classLoader, String methodReference) {
        int separator = methodReference.lastIndexOf('#');
        if (separator <= 0 || separator == methodReference.length() - 1) {
            throw new IllegalArgumentException("方法依赖必须使用 完整类名#方法名 格式: " + methodReference);
        }
        String className = methodReference.substring(0, separator);
        String methodName = methodReference.substring(separator + 1);
        try {
            Class<?> targetType = Class.forName(className, false, classLoader);
            for (java.lang.reflect.Method method : targetType.getMethods()) {
                if (method.getName().equals(methodName)) {
                    return true;
                }
            }
            return false;
        } catch (ClassNotFoundException exception) {
            return false;
        } catch (LinkageError error) {
            return false;
        }
    }

    public interface Registrar {
        void register(JavaTypes.Builder builder);
    }
}
