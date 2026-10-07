package com.novalang.runtime;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/** 普通脚本实例的回调生命周期；关闭实例后拒绝调用并注销全部宿主资源。 */
public final class ScriptCallbackScope implements ScriptCallbackContext, AutoCloseable {
    private final ReentrantLock lock = new ReentrantLock();
    private final ArrayList<AutoCloseable> resources = new ArrayList<>();
    private volatile boolean closed;

    @Override
    public boolean isValid() {
        return !closed;
    }

    @Override
    public Object invoke(Map<String, Object> bindings, Function0<Object> action) {
        lock.lock();
        try {
            requireActive();
            try (ScriptCallbackContexts.ContextHandle installed = ScriptCallbackContexts.install(this)) {
                return action.invoke();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public AutoCloseable register(AutoCloseable resource) {
        if (resource == null) {
            throw new IllegalArgumentException("resource must not be null");
        }
        lock.lock();
        try {
            requireActive();
            resources.add(resource);
        } finally {
            lock.unlock();
        }
        return () -> {
            lock.lock();
            try {
                resources.remove(resource);
            } finally {
                lock.unlock();
            }
        };
    }

    private void requireActive() {
        if (closed) {
            throw new IllegalStateException("Script instance is closed");
        }
    }

    @Override
    public void close() {
        ArrayList<AutoCloseable> snapshot;
        lock.lock();
        try {
            if (closed) {
                return;
            }
            closed = true;
            snapshot = new ArrayList<>(resources);
            resources.clear();
        } finally {
            lock.unlock();
        }
        IllegalStateException failure = null;
        for (int index = snapshot.size() - 1; index >= 0; index--) {
            try {
                snapshot.get(index).close();
            } catch (Exception exception) {
                if (failure == null) {
                    failure = new IllegalStateException("Script resource disposal failed", exception);
                } else {
                    failure.addSuppressed(exception);
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
    }
}
