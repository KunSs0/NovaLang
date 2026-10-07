package com.novalang.runtime;

import java.util.Map;

/** 脚本实例提供的回调执行和资源所有者，上层运行环境可提供自己的实现。 */
public interface ScriptCallbackContext {
    boolean isValid();
    Object invoke(Map<String, Object> bindings, Function0<Object> action);
    AutoCloseable register(AutoCloseable resource);
}
