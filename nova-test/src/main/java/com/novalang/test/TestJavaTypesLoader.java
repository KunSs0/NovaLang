package com.novalang.test;

import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/** 从测试依赖 JAR 中加载 JavaTypes 工厂。 */
public final class TestJavaTypesLoader {
    public List<JavaTypes> load(List<JavaTypesFactorySpec> factories, ClassLoader classLoader) {
        if (factories == null) {
            throw new IllegalArgumentException("factories must not be null");
        }
        if (classLoader == null) {
            throw new IllegalArgumentException("classLoader must not be null");
        }
        List<JavaTypes> result = new ArrayList<JavaTypes>();
        for (JavaTypesFactorySpec factory : factories) {
            result.add(loadOne(factory, classLoader));
        }
        return result;
    }

    private JavaTypes loadOne(JavaTypesFactorySpec factory, ClassLoader classLoader) {
        try {
            Class<?> type = Class.forName(factory.getClassName(), true, classLoader);
            Method method = type.getMethod(factory.getMethodName());
            Object receiver = null;
            if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                Field instanceField = type.getField("INSTANCE");
                receiver = instanceField.get(null);
                if (receiver == null) {
                    throw new IllegalArgumentException("JavaTypes factory INSTANCE must not be null: "
                            + factory.getClassName());
                }
            }
            Object value = method.invoke(receiver);
            if (!(value instanceof JavaTypes)) {
                throw new IllegalArgumentException("JavaTypes factory returned "
                        + (value == null ? "null" : value.getClass().getName()));
            }
            return (JavaTypes) value;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException("Failed to load JavaTypes factory: "
                    + factory.getClassName() + "#" + factory.getMethodName(), exception);
        }
    }
}
