package com.novalang.workspace;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 可由监听器、任务、RPC 或 Provider 稳定持有的 Nova 函数回调。
 *
 * <p>回调只保存入口标识、捕获绑定、资源作用域和所属 Generation，不直接持有编译器
 * 内部函数对象。旧 Workspace 销毁后，调用会在进入脚本前明确失败。</p>
 */
public final class NovaCallback {

    private final WorkspaceGeneration generation;
    private final String entryName;
    private final String functionName;
    private final Map<String, Object> capturedBindings;
    private final ResourceScope scope;
    private final ExecutionPolicy policy;

    /**
     * 创建稳定回调。
     *
     * @param generation 所属代际
     * @param entryName 入口名称
     * @param functionName 函数名称
     * @param capturedBindings 捕获绑定
     * @param scope 资源所有者作用域
     * @param policy 固定执行策略
     */
    NovaCallback(WorkspaceGeneration generation,
                 String entryName,
                 String functionName,
                 Map<String, Object> capturedBindings,
                 ResourceScope scope,
                 ExecutionPolicy policy) {
        this.generation = generation;
        this.entryName = entryName;
        this.functionName = functionName;
        Map<String, Object> bindings = capturedBindings == null
                ? Collections.<String, Object>emptyMap() : capturedBindings;
        this.capturedBindings = Collections.unmodifiableMap(
                new LinkedHashMap<String, Object>(bindings));
        this.scope = scope;
        this.policy = policy;
    }

    /** @return 所属代际标识 */
    public long getGenerationId() {
        return generation.getId();
    }

    /** @return 回调入口名称 */
    public String getEntryName() {
        return entryName;
    }

    /** @return 回调函数名称 */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * 判断回调当前是否仍可执行。
     *
     * @return Generation 和 ResourceScope 均处于 ACTIVE 时返回 {@code true}
     */
    public boolean isValid() {
        return generation.getState() == GenerationState.ACTIVE
                && scope.getState() == ResourceScopeState.ACTIVE;
    }

    /**
     * 仅使用捕获绑定执行回调。
     *
     * @param arguments 函数参数
     * @return 函数返回值
     */
    public Object invoke(Object... arguments) {
        return invokeWithBindings(Collections.<String, Object>emptyMap(), arguments);
    }

    /**
     * 合并捕获绑定和本次调用绑定后执行回调。
     *
     * <p>同名项以本次调用绑定为准；两个输入映射都不会被脚本修改。</p>
     *
     * @param invocationBindings 本次调用绑定
     * @param arguments 函数参数
     * @return 函数返回值
     */
    public Object invokeWithBindings(Map<String, Object> invocationBindings, Object... arguments) {
        if (invocationBindings == null) {
            throw new IllegalArgumentException("invocationBindings must not be null");
        }
        Map<String, Object> merged = new LinkedHashMap<String, Object>(capturedBindings);
        merged.putAll(invocationBindings);
        return generation.invoke(entryName, functionName, merged, scope, policy, arguments);
    }
}
