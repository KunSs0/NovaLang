package com.novalang.runtime.resolution;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * 将非公开实现类上的公共实例方法规范化为可访问的公共父类或接口声明。
 *
 * <p>JDK 集合视图等对象经常由非公开实现类实现。例如
 * {@code HashMap.values()} 的运行时类型是 {@code HashMap$Values}，其
 * {@code size()} 方法声明在这个非公开类上。直接对该声明建立
 * {@link java.lang.invoke.MethodHandle} 会在模块边界处失败；使用公共接口
 * {@code Collection.size()} 的声明则仍然保留正常的虚调用分派。</p>
 */
public final class PublicMethodResolver {

    private PublicMethodResolver() {}

    /**
     * 返回可用于公共 MethodHandle 查找的实例方法声明。
     *
     * @param method 已选中的实例方法
     * @return 原方法、公共父类/接口中的同签名声明；若不存在可访问声明则返回 null
     */
    public static Method resolvePublicDeclaration(Method method) {
        if (method == null) {
            return null;
        }
        if (Modifier.isStatic(method.getModifiers())) {
            return null;
        }
        if (isPublicType(method.getDeclaringClass())
                && Modifier.isPublic(method.getModifiers())) {
            return method;
        }

        List<Class<?>> ancestors = collectAncestors(method.getDeclaringClass());
        for (Class<?> ancestor : ancestors) {
            if (!isPublicType(ancestor)) {
                continue;
            }
            Method declaration = findDeclaredMethod(ancestor, method.getName(), method.getParameterTypes());
            if (declaration != null) {
                return declaration;
            }
        }
        return null;
    }

    private static List<Class<?>> collectAncestors(Class<?> type) {
        List<Class<?>> result = new ArrayList<Class<?>>();
        Set<Class<?>> visited = new HashSet<Class<?>>();
        Queue<Class<?>> pending = new ArrayDeque<Class<?>>();
        enqueueParents(type, pending);

        while (!pending.isEmpty()) {
            Class<?> current = pending.remove();
            if (!visited.add(current)) {
                continue;
            }
            result.add(current);
            enqueueParents(current, pending);
        }
        return result;
    }

    private static void enqueueParents(Class<?> type, Queue<Class<?>> pending) {
        Class<?> superclass = type.getSuperclass();
        if (superclass != null) {
            pending.add(superclass);
        }

        Class<?>[] interfaces = type.getInterfaces();
        Arrays.sort(interfaces, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> left, Class<?> right) {
                return left.getName().compareTo(right.getName());
            }
        });
        for (Class<?> iface : interfaces) {
            pending.add(iface);
        }
    }

    private static Method findDeclaredMethod(Class<?> type, String name, Class<?>[] parameterTypes) {
        Method[] declaredMethods = type.getDeclaredMethods();
        List<Method> matches = new ArrayList<Method>();
        for (Method candidate : declaredMethods) {
            if (!name.equals(candidate.getName())) {
                continue;
            }
            if (!Arrays.equals(parameterTypes, candidate.getParameterTypes())) {
                continue;
            }
            if (!Modifier.isPublic(candidate.getModifiers())
                    || Modifier.isStatic(candidate.getModifiers())) {
                continue;
            }
            matches.add(candidate);
        }
        if (matches.isEmpty()) {
            return null;
        }
        Collections.sort(matches, new Comparator<Method>() {
            @Override
            public int compare(Method left, Method right) {
                if (left.isBridge() != right.isBridge()) {
                    return left.isBridge() ? 1 : -1;
                }
                if (left.isSynthetic() != right.isSynthetic()) {
                    return left.isSynthetic() ? 1 : -1;
                }
                return left.getReturnType().getName().compareTo(right.getReturnType().getName());
            }
        });
        return matches.get(0);
    }

    private static boolean isPublicType(Class<?> type) {
        return Modifier.isPublic(type.getModifiers());
    }
}
