package com.novalang.runtime;

/**
 * 四参数函数接口
 */
@FunctionalInterface
public interface Function4<T1, T2, T3, T4, R> extends ScriptFunction {
    R invoke(T1 arg1, T2 arg2, T3 arg3, T4 arg4);

    @Override
    default int argumentCount() {
        return 4;
    }

    @Override
    @SuppressWarnings("unchecked")
    default Object invokeArguments(Object... arguments) {
        if (arguments == null || arguments.length != 4) {
            throw new IllegalArgumentException("Function4 requires 4 arguments");
        }
        return invoke((T1) arguments[0], (T2) arguments[1], (T3) arguments[2], (T4) arguments[3]);
    }
}
