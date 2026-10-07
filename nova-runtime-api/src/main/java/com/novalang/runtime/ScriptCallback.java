package com.novalang.runtime;

import java.util.Collections;
import java.util.Map;

/** 由脚本实例持有的函数回调；宿主注册 API 内部捕获，业务调用者直接传函数值。 */
public final class ScriptCallback {
    private final ScriptFunction function;
    private final ScriptCallbackContext context;
    private final NovaScriptContext script;

    ScriptCallback(ScriptFunction function) {
        if (function == null) {
            throw new IllegalArgumentException("function must not be null");
        }
        this.function = function;
        this.context = ScriptCallbackContexts.require();
        NovaScriptContext currentScript = NovaScriptContext.current();
        if (currentScript == null || !context.isValid()) {
            throw new IllegalStateException("Callback requires an active script instance");
        }
        this.script = currentScript.withBindings(Collections.emptyMap());
    }

    public boolean isValid() {
        return context.isValid();
    }

    public AutoCloseable register(AutoCloseable resource) {
        return context.register(resource);
    }

    public Object invoke(Object... arguments) {
        return invokeWithBindings(Collections.emptyMap(), arguments);
    }

    public Object invokeWithBindings(Map<String, Object> bindings, Object... arguments) {
        return invokeInContext(context, bindings, arguments);
    }

    /** 宿主可指定本次执行的子生命周期；原始脚本实例仍必须有效。 */
    public Object invokeInContext(ScriptCallbackContext executionContext,
                                  Map<String, Object> bindings, Object... arguments) {
        if (executionContext == null || !context.isValid()) {
            throw new IllegalStateException("Callback script instance is closed");
        }
        if (arguments == null || arguments.length != function.argumentCount()) {
            throw new IllegalArgumentException("Script function expects " + function.argumentCount()
                    + " arguments, received " + (arguments == null ? "null" : arguments.length));
        }
        if (bindings == null) {
            throw new IllegalArgumentException("bindings must not be null");
        }
        Map<String, Object> actualBindings = bindings;
        return executionContext.invoke(actualBindings, () -> {
            NovaScriptContext previous = NovaScriptContext.current();
            NovaScriptContext.setCurrent(script.withBindings(actualBindings));
            try {
                return function.invokeArguments(arguments);
            } finally {
                NovaScriptContext.setCurrent(previous);
            }
        });
    }
}
