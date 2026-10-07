package com.novalang.runtime;

/** 底层函数值契约；参数和返回值保持 Java 原对象，不依赖任何工作区实现。 */
public interface ScriptFunction {
    int argumentCount();
    Object invokeArguments(Object... arguments);
}
