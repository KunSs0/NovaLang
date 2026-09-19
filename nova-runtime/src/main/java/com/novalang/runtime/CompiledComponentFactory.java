package com.novalang.runtime;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/** 一个隔离程序作用域中的已绑定无参数组件工厂；多次 create 仅共享该作用域的模块状态。 */
public final class CompiledComponentFactory<T> {
    private final CompiledNova program;
    private final MethodHandle entry;
    private final Class<T> componentType;
    private final String functionName;

    CompiledComponentFactory(CompiledNova program, Class<?> owner, String functionName, Class<T> type) {
        this.program = program;
        this.componentType = type;
        this.functionName = functionName;
        try {
            Method method = owner.getMethod(functionName);
            if (!Modifier.isStatic(method.getModifiers()) || method.getReturnType() == Void.TYPE) {
                throw new IllegalArgumentException("Component factory must be a non-void static function: " + functionName);
            }
            this.entry = MethodHandles.lookup().unreflect(method).asType(MethodType.methodType(Object.class));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException("Missing no-argument component factory: " + functionName, exception);
        }
    }

    public CompiledNova getProgram() {
        return program;
    }

    public T create() {
        Object value = program.invokeComponentFactory(entry, functionName);
        if (!componentType.isInstance(value)) {
            throw new IllegalStateException("Component factory " + functionName + " must return " + componentType.getName());
        }
        return componentType.cast(value);
    }
}
