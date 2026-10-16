package com.novalang.runtime.compilation;

/**
 * 编译配置、模块解析、字节码链接或源码映射诊断中的明确失败。
 * <p>保留原始原因和原始源码位置，不切换到其他执行模式。</p>
 */
public final class CompilationException extends RuntimeException {

    /**
     * 使用明确错误消息创建异常。
     *
     * @param message 面向接入方的错误消息
     */
    public CompilationException(String message) {
        super(message);
    }

    /**
     * 使用错误消息及原始原因创建异常。
     *
     * @param message 面向接入方的错误消息
     * @param cause 原始异常
     */
    public CompilationException(String message, Throwable cause) {
        super(message, cause);
    }
}
