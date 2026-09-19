package com.novalang.runtime.host;

/**
 * 按 Java 接收者类型注册的扩展函数描述。
 */
public final class JavaExtensionDescriptor {
    private final Class<?> targetType;
    private final JavaFunctionDescriptor function;

    public JavaExtensionDescriptor(Class<?> targetType, JavaFunctionDescriptor function) {
        if (targetType == null) {
            throw new IllegalArgumentException("targetType must not be null");
        }
        if (function == null) {
            throw new IllegalArgumentException("function must not be null");
        }
        this.targetType = targetType;
        this.function = function;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public JavaFunctionDescriptor getFunction() {
        return function;
    }
}
