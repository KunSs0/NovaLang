package com.novalang.runtime;

/**
 * 八参数函数接口
 */
@FunctionalInterface
public interface Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> extends ScriptFunction {
    R invoke(T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5, T6 arg6, T7 arg7, T8 arg8);

    @Override
    default int argumentCount() {
        return 8;
    }

    @Override
    @SuppressWarnings("unchecked")
    default Object invokeArguments(Object... arguments) {
        if (arguments == null || arguments.length != 8) {
            throw new IllegalArgumentException("Function8 requires 8 arguments");
        }
        return invoke((T1) arguments[0], (T2) arguments[1], (T3) arguments[2], (T4) arguments[3], (T5) arguments[4], (T6) arguments[5], (T7) arguments[6], (T8) arguments[7]);
    }
}
