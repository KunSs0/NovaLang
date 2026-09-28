package com.novalang.test;

/** 描述由一个业务 JAR 提供的 JavaTypes 工厂。 */
public final class JavaTypesFactorySpec {
    private final String className;
    private final String methodName;

    public JavaTypesFactorySpec(String className, String methodName) {
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalArgumentException("JavaTypes factory class must not be blank");
        }
        if (methodName == null || methodName.trim().isEmpty()) {
            throw new IllegalArgumentException("JavaTypes factory method must not be blank");
        }
        this.className = className;
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }
}
