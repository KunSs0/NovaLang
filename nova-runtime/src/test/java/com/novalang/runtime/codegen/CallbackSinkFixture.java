package com.novalang.runtime.codegen;

/**
 * 模拟 Tween、事件总线等会长期持有脚本回调的宿主对象。
 */
public final class CallbackSinkFixture {

    private Runnable callback;

    public void install(Runnable callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        this.callback = callback;
    }

    public boolean hasCallback() {
        return callback != null;
    }

    public String getCallbackClassName() {
        if (callback == null) {
            throw new IllegalStateException("callback is not installed");
        }
        return callback.getClass().getName();
    }

    public void fire() {
        if (callback == null) {
            throw new IllegalStateException("callback is not installed");
        }
        callback.run();
    }

    public void clear() {
        callback = null;
    }
}
