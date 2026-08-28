package com.novalang.runtime;

/**
 * 当前线程的可选调度任务上下文。
 */
public final class NovaScheduleContexts {

    private static final ThreadLocal<NovaScheduleContext> CURRENT = new ThreadLocal<NovaScheduleContext>();

    /**
     * 工具类不允许实例化。
     */
    private NovaScheduleContexts() {
    }

    /**
     * 获取当前任务上下文。
     *
     * @return 当前上下文；普通宿主脚本调用时返回 {@code null}
     */
    public static NovaScheduleContext current() {
        return CURRENT.get();
    }

    /**
     * 安装当前调用的任务上下文。
     *
     * @param context 要安装的上下文
     * @return 必须在当前线程关闭的恢复句柄
     */
    public static ContextHandle install(NovaScheduleContext context) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        NovaScheduleContext previous = CURRENT.get();
        CURRENT.set(context);
        return new ContextHandle(previous, Thread.currentThread());
    }

    /**
     * 恢复此前任务上下文的关闭句柄。
     */
    public static final class ContextHandle implements AutoCloseable {
        private final NovaScheduleContext previous;
        private final Thread ownerThread;
        private boolean closed;

        ContextHandle(NovaScheduleContext previous, Thread ownerThread) {
            this.previous = previous;
            this.ownerThread = ownerThread;
        }

        /**
         * 恢复安装前的任务上下文。
         */
        @Override
        public void close() {
            if (closed) {
                return;
            }
            if (Thread.currentThread() != ownerThread) {
                throw new IllegalStateException("NovaScheduleContexts must be closed on its installing thread");
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
