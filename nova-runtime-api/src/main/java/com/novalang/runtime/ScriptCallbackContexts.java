package com.novalang.runtime;

/** 当前底层脚本实例的回调所有者。 */
public final class ScriptCallbackContexts {
    private static final ThreadLocal<ScriptCallbackContext> CURRENT = new ThreadLocal<>();

    private ScriptCallbackContexts() { }

    public static ScriptCallbackContext current() {
        return CURRENT.get();
    }

    public static ScriptCallbackContext require() {
        ScriptCallbackContext context = CURRENT.get();
        if (context == null) {
            throw new IllegalStateException("No active script callback context");
        }
        return context;
    }

    public static ContextHandle install(ScriptCallbackContext context) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        ScriptCallbackContext previous = CURRENT.get();
        CURRENT.set(context);
        return new ContextHandle(previous, Thread.currentThread());
    }

    public static final class ContextHandle implements AutoCloseable {
        private final ScriptCallbackContext previous;
        private final Thread thread;
        private boolean closed;

        private ContextHandle(ScriptCallbackContext previous, Thread thread) {
            this.previous = previous;
            this.thread = thread;
        }

        @Override
        public void close() {
            if (closed) {
                return;
            }
            if (thread != Thread.currentThread()) {
                throw new IllegalStateException("Script callback context must be closed on its installing thread");
            }
            closed = true;
            if (previous == null) {
                CURRENT.remove();
            } else {
                CURRENT.set(previous);
            }
        }
    }
}
