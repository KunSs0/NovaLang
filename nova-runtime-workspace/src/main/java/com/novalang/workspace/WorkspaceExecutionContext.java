package com.novalang.workspace;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 当前线程的 Workspace 执行上下文。
 *
 * <p>Host API 通过该类获取当前 ResourceScope 并自动登记资源。回调跨线程执行时会
 * 显式重新安装上下文，因此不依赖线程池中残留的 ThreadLocal。</p>
 */
public final class WorkspaceExecutionContext {

    private static final ThreadLocal<Frame> CURRENT = new ThreadLocal<Frame>();

    /**
     * 工具类不允许实例化。
     */
    private WorkspaceExecutionContext() {
    }

    /**
     * 获取当前资源作用域。
     *
     * @return 当前作用域；不在 Workspace 调用中时返回 {@code null}
     */
    public static ResourceScope currentScope() {
        Frame frame = CURRENT.get();
        return frame == null ? null : frame.scope;
    }

    /**
     * 获取当前资源作用域，不存在时直接失败。
     *
     * @return 当前资源作用域
     */
    public static ResourceScope requireScope() {
        ResourceScope scope = currentScope();
        if (scope == null) {
            throw new WorkspaceException("The current thread has no Workspace ResourceScope");
        }
        return scope;
    }

    /**
     * 获取当前 Workspace Generation。
     *
     * @return 当前代际；不在 Workspace 调用中时返回 {@code null}
     */
    public static WorkspaceGeneration currentGeneration() {
        Frame frame = CURRENT.get();
        return frame == null ? null : frame.generation;
    }

    /**
     * 获取当前不可变绑定快照。
     *
     * @return 当前绑定；不在 Workspace 调用中时返回空映射
     */
    public static Map<String, Object> currentBindings() {
        Frame frame = CURRENT.get();
        if (frame == null) {
            return Collections.emptyMap();
        }
        return frame.bindings;
    }

    /**
     * 安装一次执行上下文，并在关闭句柄时恢复此前上下文。
     *
     * @param generation 当前代际
     * @param scope 当前资源作用域
     * @param bindings 本次调用绑定
     * @return 必须在当前线程关闭的恢复句柄
     */
    static ContextHandle install(WorkspaceGeneration generation,
                                 ResourceScope scope,
                                 Map<String, Object> bindings) {
        if (generation == null || scope == null || bindings == null) {
            throw new IllegalArgumentException("generation, scope and bindings must not be null");
        }
        Frame previous = CURRENT.get();
        Map<String, Object> snapshot = Collections.unmodifiableMap(
                new LinkedHashMap<String, Object>(bindings));
        CURRENT.set(new Frame(generation, scope, snapshot));
        return new ContextHandle(previous, Thread.currentThread());
    }

    /**
     * 当前上下文帧。
     */
    private static final class Frame {
        private final WorkspaceGeneration generation;
        private final ResourceScope scope;
        private final Map<String, Object> bindings;

        Frame(WorkspaceGeneration generation, ResourceScope scope, Map<String, Object> bindings) {
            this.generation = generation;
            this.scope = scope;
            this.bindings = bindings;
        }
    }

    /**
     * 恢复前一执行上下文的关闭句柄。
     */
    static final class ContextHandle implements AutoCloseable {
        private final Frame previous;
        private final Thread ownerThread;
        private boolean closed;

        ContextHandle(Frame previous, Thread ownerThread) {
            this.previous = previous;
            this.ownerThread = ownerThread;
        }

        /**
         * 恢复安装前的上下文。
         */
        @Override
        public void close() {
            if (closed) {
                return;
            }
            if (Thread.currentThread() != ownerThread) {
                throw new WorkspaceException("WorkspaceExecutionContext must be closed on its installing thread");
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
