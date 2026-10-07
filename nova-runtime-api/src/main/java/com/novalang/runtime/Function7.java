package com.novalang.runtime;

/**
 * 七参数函数接口
 */
@FunctionalInterface
public interface Function7<T1, T2, T3, T4, T5, T6, T7, R> extends ScriptFunction {
    R invoke(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7);

    @Override
    default int argumentCount() {
        return 7;
    }

    @Override
    @SuppressWarnings("unchecked")
    default Object invokeArguments(Object... arguments) {
        if (arguments == null || arguments.length != 7) {
            throw new IllegalArgumentException("Function7 requires 7 arguments");
        }
        return invoke((T1) arguments[0], (T2) arguments[1], (T3) arguments[2], (T4) arguments[3], (T5) arguments[4], (T6) arguments[5], (T7) arguments[6]);
    }
}
