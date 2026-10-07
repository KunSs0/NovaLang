package com.novalang.runtime;

/**
 * 三参数函数接口
 *
 * @param <T1> 第一个参数类型
 * @param <T2> 第二个参数类型
 * @param <T3> 第三个参数类型
 * @param <R>  返回类型
 */
@FunctionalInterface
public interface Function3<T1, T2, T3, R> extends ScriptFunction {
    R invoke(T1 arg1, T2 arg2, T3 arg3);

    @Override
    default int argumentCount() {
        return 3;
    }

    @Override
    @SuppressWarnings("unchecked")
    default Object invokeArguments(Object... arguments) {
        if (arguments == null || arguments.length != 3) {
            throw new IllegalArgumentException("Function3 requires 3 arguments");
        }
        return invoke((T1) arguments[0], (T2) arguments[1], (T3) arguments[2]);
    }
}
