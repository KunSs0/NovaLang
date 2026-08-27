package com.novalang.workspace;

import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/**
 * 按固定策略执行 Workspace 程序的内部调度器适配器。
 */
final class WorkspaceExecutionDispatcher {

    /**
     * 工具类不允许实例化。
     */
    private WorkspaceExecutionDispatcher() {
    }

    /**
     * 根据策略执行任务，不进行异常驱动的线程切换。
     *
     * @param policy 固定执行策略
     * @param scope 当前资源作用域
     * @param action 脚本调用
     * @param <T> 返回值类型
     * @return 脚本调用结果
     * @throws WorkspaceException 调度器、作用域或脚本调用失败时抛出
     */
    static <T> T execute(ExecutionPolicy policy,
                         ResourceScope scope,
                         Callable<T> action) {
        if (policy == ExecutionPolicy.MAIN_THREAD) {
            return executeOnMainThread(action);
        }
        if (policy == ExecutionPolicy.SERIAL_SCOPE) {
            try {
                return scope.executeSerial(action);
            } catch (RuntimeException exception) {
                throw exception;
            } catch (Exception exception) {
                throw new WorkspaceException("Serial scope execution failed", exception);
            }
        }
        try {
            return action.call();
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new WorkspaceException("Workspace execution failed", exception);
        }
    }

    /**
     * 在全局 Nova 调度器的主线程执行并同步取得结果。
     *
     * @param action 脚本调用
     * @param <T> 返回值类型
     * @return 脚本调用结果
     * @throws WorkspaceException 调度器不可用、线程被中断或调用失败时抛出
     */
    private static <T> T executeOnMainThread(Callable<T> action) {
        NovaScheduler scheduler = SchedulerHolder.get();
        if (scheduler == null) {
            throw new WorkspaceException("The global NovaScheduler is not installed");
        }
        if (scheduler.isMainThread()) {
            try {
                return action.call();
            } catch (RuntimeException exception) {
                throw exception;
            } catch (Exception exception) {
                throw new WorkspaceException("Main-thread Workspace execution failed", exception);
            }
        }

        Executor executor = scheduler.mainExecutor();
        if (executor == null) {
            throw new WorkspaceException("The global NovaScheduler has no main executor");
        }
        FutureTask<T> task = new FutureTask<T>(action);
        executor.execute(task);
        try {
            return task.get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new WorkspaceException("Interrupted while waiting for main-thread Workspace execution", exception);
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new WorkspaceException("Main-thread Workspace execution failed", cause);
        }
    }
}
