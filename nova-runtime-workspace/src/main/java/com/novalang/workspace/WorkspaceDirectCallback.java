package com.novalang.workspace;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 由宿主长期持有的显式字节码回调。
 *
 * <p>该类型保存 Nova 编译类实现的接口实例，而不是解释器内部函数对象；调用时会恢复
 * 创建时的 Generation、ResourceScope、绑定快照与执行策略。</p>
 */
public final class WorkspaceDirectCallback {

    private final WorkspaceGeneration generation;
    private final ResourceScope scope;
    private final Map<String, Object> capturedBindings;
    private final ExecutionPolicy policy;
    private final WorkspaceEventCallback callback;

    WorkspaceDirectCallback(WorkspaceGeneration generation,
                            ResourceScope scope,
                            Map<String, Object> capturedBindings,
                            ExecutionPolicy policy,
                            WorkspaceEventCallback callback) {
        this.generation = generation;
        this.scope = scope;
        Map<String, Object> bindings = capturedBindings == null
                ? Collections.<String, Object>emptyMap() : capturedBindings;
        this.capturedBindings = Collections.unmodifiableMap(
                new LinkedHashMap<String, Object>(bindings));
        this.policy = policy;
        this.callback = callback;
    }

    /** @return Generation 与资源作用域均处于活动状态时返回 {@code true} */
    public boolean isValid() {
        return generation.getState() == GenerationState.ACTIVE
                && scope.getState() == ResourceScopeState.ACTIVE;
    }

    /**
     * 在捕获的 Workspace 上下文中调用回调。
     *
     * @param value 宿主传入的事件或负载
     * @return 监听器返回值
     */
    public Object invoke(Object value) {
        return generation.invokeDirectCallback(scope, capturedBindings, policy, callback, value);
    }
}
