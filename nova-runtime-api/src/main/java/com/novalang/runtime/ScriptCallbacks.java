package com.novalang.runtime;

/** 宿主 API 捕获底层脚本函数的通用入口。 */
public final class ScriptCallbacks {
    private ScriptCallbacks() { }

    public static ScriptCallback bind(ScriptFunction function) {
        return new ScriptCallback(function);
    }
}
