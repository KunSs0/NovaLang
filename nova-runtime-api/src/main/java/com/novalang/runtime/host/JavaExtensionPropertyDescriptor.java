package com.novalang.runtime.host;

/**
 * 面向某个 Java 接收者类型注册的扩展属性。
 *
 * <p>getter 的参数数组为 {@code [receiver]}，setter 为
 * {@code [receiver, value]}。没有 setter 的属性为只读属性。</p>
 */
public final class JavaExtensionPropertyDescriptor {
    private static final String GETTER_PREFIX = "$novaProperty$get$";
    private static final String SETTER_PREFIX = "$novaProperty$set$";

    private final Class<?> targetType;
    private final JavaPropertyDescriptor property;
    private final JavaFunctionInvoker getter;
    private final JavaFunctionInvoker setter;

    public JavaExtensionPropertyDescriptor(Class<?> targetType,
                                           JavaPropertyDescriptor property,
                                           JavaFunctionInvoker getter,
                                           JavaFunctionInvoker setter) {
        if (targetType == null) {
            throw new IllegalArgumentException("targetType must not be null");
        }
        if (property == null) {
            throw new IllegalArgumentException("property must not be null");
        }
        if (getter == null) {
            throw new IllegalArgumentException("extension property getter must not be null");
        }
        if (property.isMutable() && setter == null) {
            throw new IllegalArgumentException("mutable extension property setter must not be null");
        }
        this.targetType = targetType;
        this.property = property;
        this.getter = getter;
        this.setter = setter;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public JavaPropertyDescriptor getProperty() {
        return property;
    }

    public JavaFunctionInvoker getGetter() {
        return getter;
    }

    public JavaFunctionInvoker getSetter() {
        return setter;
    }

    public static String getterExtensionName(String propertyName) {
        return GETTER_PREFIX + propertyName;
    }

    public static String setterExtensionName(String propertyName) {
        return SETTER_PREFIX + propertyName;
    }
}
