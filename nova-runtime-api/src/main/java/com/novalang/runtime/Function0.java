package com.novalang.runtime;

import java.util.function.Supplier;

/**
 * 无参数函数接口。
 * 兼容 Java 的 Supplier 和 Runnable。
 *
 * @param <R> 返回类型
 */
@FunctionalInterface
public interface Function0<R> extends Supplier<R>, Runnable, ScriptFunction {
    R invoke();

    @Override
    default int argumentCount() {
        return 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    default Object invokeArguments(Object... arguments) {
        if (arguments == null || arguments.length != 0) {
            throw new IllegalArgumentException("Function0 requires 0 arguments");
        }
        return invoke();
    }

    @Override
    default R get() {
        return invoke();
    }

    @Override
    default void run() {
        invoke();
    }
}
