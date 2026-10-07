package com.novalang.workspace;

import com.novalang.runtime.Function0;
import com.novalang.runtime.ScriptCallbackContext;
import java.util.LinkedHashMap;
import java.util.Map;

/** 上层 Workspace 对底层脚本回调协议的适配，不向业务接口暴露 Workspace 回调类型。 */
public final class WorkspaceCallbackContext implements ScriptCallbackContext {
    private final WorkspaceGeneration generation;
    private final ResourceScope scope;
    private final Map<String, Object> capturedBindings;

    public WorkspaceCallbackContext(WorkspaceGeneration generation, ResourceScope scope, Map<String, Object> bindings) {
        this.generation = generation;
        this.scope = scope;
        this.capturedBindings = new LinkedHashMap<>(bindings);
    }

    @Override
    public boolean isValid() {
        GenerationState state = generation.getState();
        return (state == GenerationState.LOADING || state == GenerationState.ACTIVE)
                && scope.getState() == ResourceScopeState.ACTIVE;
    }

    @Override
    public Object invoke(Map<String, Object> bindings, Function0<Object> action) {
        Map<String, Object> merged = new LinkedHashMap<>(capturedBindings);
        merged.putAll(bindings);
        return generation.invokeDirectCallback(scope, merged, null, value -> action.invoke(), null);
    }

    @Override
    public AutoCloseable register(AutoCloseable resource) {
        if (!isValid()) {
            throw new IllegalStateException("Script callback owner is closed");
        }
        WorkspaceResource owned = () -> {
            try {
                resource.close();
            } catch (RuntimeException exception) {
                throw exception;
            } catch (Exception exception) {
                throw new IllegalStateException("Script resource disposal failed", exception);
            }
        };
        scope.register(owned);
        return () -> scope.unregister(owned);
    }
}
