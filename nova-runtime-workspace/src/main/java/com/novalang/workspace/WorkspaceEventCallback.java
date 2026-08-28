package com.novalang.workspace;

/**
 * 可由 Workspace 捕获并在后续调用时恢复其执行上下文的单参数回调。
 */
@FunctionalInterface
public interface WorkspaceEventCallback {

    /**
     * 处理宿主传入的单个值。
     *
     * @param value 宿主事件或回调负载
     * @return 回调返回值
     */
    Object invoke(Object value);
}
