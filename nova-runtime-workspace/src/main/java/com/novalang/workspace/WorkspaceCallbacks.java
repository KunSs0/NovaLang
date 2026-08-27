package com.novalang.workspace;

import java.util.Map;

/**
 * 从当前 Workspace 执行上下文创建稳定 Nova 回调的通用入口。
 */
public final class WorkspaceCallbacks {

    /**
     * 工具类不允许实例化。
     */
    private WorkspaceCallbacks() {
    }

    /**
     * 使用 Workspace 默认执行策略创建稳定回调。
     *
     * @param entryName 回调所属入口名称
     * @param functionName 回调函数名称
     * @return 绑定当前 Generation、ResourceScope 和调用绑定的稳定回调
     */
    public static NovaCallback create(String entryName, String functionName) {
        return create(entryName, functionName, null);
    }

    /**
     * 使用指定执行策略创建稳定回调。
     *
     * @param entryName 回调所属入口名称
     * @param functionName 回调函数名称
     * @param policy 固定执行策略；传 {@code null} 时使用 Workspace 默认策略
     * @return 绑定当前 Generation、ResourceScope 和调用绑定的稳定回调
     */
    public static NovaCallback create(String entryName,
                                      String functionName,
                                      ExecutionPolicy policy) {
        if (entryName == null || entryName.trim().isEmpty()) {
            throw new IllegalArgumentException("entryName must not be blank");
        }
        if (functionName == null || functionName.trim().isEmpty()) {
            throw new IllegalArgumentException("functionName must not be blank");
        }
        WorkspaceGeneration generation = WorkspaceExecutionContext.currentGeneration();
        if (generation == null) {
            throw new WorkspaceException("The current thread has no Workspace Generation");
        }
        ResourceScope scope = WorkspaceExecutionContext.requireScope();
        Map<String, Object> capturedBindings = WorkspaceExecutionContext.currentBindings();
        // 只捕获不可变绑定快照，不保存当前编译器函数对象。
        return generation.createCallback(entryName, functionName,
                capturedBindings, scope, policy);
    }
}
