package com.novalang.workspace;

/**
 * Workspace 配置、解析、编译或生命周期操作失败时抛出的统一异常。
 *
 * <p>该异常只包装能够由 Workspace 明确归因的错误，不会把加载失败转换为旧代际
 * 或其他运行时的回退行为。</p>
 */
public final class WorkspaceException extends RuntimeException {

    /**
     * 使用明确错误消息创建异常。
     *
     * @param message 面向接入方的错误消息
     */
    public WorkspaceException(String message) {
        super(message);
    }

    /**
     * 使用错误消息及原始原因创建异常。
     *
     * @param message 面向接入方的错误消息
     * @param cause 原始异常
     */
    public WorkspaceException(String message, Throwable cause) {
        super(message, cause);
    }
}
