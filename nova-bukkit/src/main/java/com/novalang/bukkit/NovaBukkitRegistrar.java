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

    public interface Registrar {
        void register(JavaTypes.Builder builder);
    }
}
