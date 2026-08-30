package com.novalang.runtime.host;

@FunctionalInterface
public interface JavaFunctionInvoker {
    Object invoke(Object... args) throws Exception;
}
