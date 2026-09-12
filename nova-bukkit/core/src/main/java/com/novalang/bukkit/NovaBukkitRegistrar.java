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
        String methodPart = methodReference.substring(separator + 1);
        try {
            Class<?> targetType = Class.forName(className, false, classLoader);
            int signatureStart = methodPart.indexOf('(');
            if (signatureStart < 0) {
                for (java.lang.reflect.Method method : targetType.getMethods()) {
                    if (method.getName().equals(methodPart)) {
                        return true;
                    }
                }
                return false;
            }
            if (!methodPart.endsWith(")")) {
                throw new IllegalArgumentException("方法依赖签名缺少右括号: " + methodReference);
            }
            String methodName = methodPart.substring(0, signatureStart);
            String parameterPart = methodPart.substring(signatureStart + 1,
                    methodPart.length() - 1);
            String[] parameterNames;
            if (parameterPart.isEmpty()) {
                parameterNames = new String[0];
            } else {
                parameterNames = parameterPart.split(",");
            }
            for (java.lang.reflect.Method method : targetType.getMethods()) {
                if (method.getName().equals(methodName)
                        && hasParameterTypes(method.getParameterTypes(), parameterNames,
                        classLoader)) {
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

    private static boolean hasParameterTypes(Class<?>[] actualTypes,
                                              String[] expectedNames,
                                              ClassLoader classLoader) {
        if (actualTypes.length != expectedNames.length) {
            return false;
        }
        for (int index = 0; index < actualTypes.length; index++) {
            Class<?> expectedType = resolveType(expectedNames[index], classLoader);
            if (expectedType == null || actualTypes[index] != expectedType) {
                return false;
            }
        }
        return true;
    }

    private static Class<?> resolveType(String name, ClassLoader classLoader) {
        if ("boolean".equals(name)) {
            return Boolean.TYPE;
        }
        if ("byte".equals(name)) {
            return Byte.TYPE;
        }
        if ("char".equals(name)) {
            return Character.TYPE;
        }
        if ("short".equals(name)) {
            return Short.TYPE;
        }
        if ("int".equals(name)) {
            return Integer.TYPE;
        }
        if ("long".equals(name)) {
            return Long.TYPE;
        }
        if ("float".equals(name)) {
            return Float.TYPE;
        }
        if ("double".equals(name)) {
            return Double.TYPE;
        }
        if ("void".equals(name)) {
            return Void.TYPE;
        }
        try {
            return Class.forName(name, false, classLoader);
        } catch (ClassNotFoundException exception) {
            return null;
        } catch (LinkageError error) {
            return null;
        }
    }

    public interface Registrar {
        void register(JavaTypes.Builder builder);
    }
}
