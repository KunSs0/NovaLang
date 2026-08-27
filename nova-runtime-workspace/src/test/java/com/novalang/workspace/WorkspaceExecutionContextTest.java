package com.novalang.workspace;

import com.novalang.runtime.Nova;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link WorkspaceExecutionContext} 安装和恢复测试。
 */
@DisplayName("Workspace 执行上下文")
class WorkspaceExecutionContextTest {

    @Test
    @DisplayName("嵌套上下文关闭后恢复外层")
    void shouldRestoreNestedContext() {
        WorkspaceGeneration generation = emptyGeneration();
        ResourceScope root = generation.getRootScope();
        LinkedHashMap<String, Object> outerBindings = new LinkedHashMap<String, Object>();
        outerBindings.put("value", 1);
        WorkspaceExecutionContext.ContextHandle outer = WorkspaceExecutionContext.install(
                generation, root, outerBindings);
        try {
            assertEquals(1, WorkspaceExecutionContext.currentBindings().get("value"));
            ResourceScope child = root.openChild(ScopeType.INVOCATION, "child");
            LinkedHashMap<String, Object> innerBindings = new LinkedHashMap<String, Object>();
            innerBindings.put("value", 2);
            WorkspaceExecutionContext.ContextHandle inner = WorkspaceExecutionContext.install(
                    generation, child, innerBindings);
            try {
                assertEquals(child, WorkspaceExecutionContext.requireScope());
                assertEquals(2, WorkspaceExecutionContext.currentBindings().get("value"));
            } finally {
                inner.close();
            }
            assertEquals(root, WorkspaceExecutionContext.requireScope());
            assertEquals(1, WorkspaceExecutionContext.currentBindings().get("value"));
        } finally {
            outer.close();
            generation.dispose();
        }
        assertNull(WorkspaceExecutionContext.currentScope());
    }

    @Test
    @DisplayName("没有上下文时 requireScope 明确失败")
    void shouldRejectMissingContext() {
        assertThrows(WorkspaceException.class, WorkspaceExecutionContext::requireScope);
    }

    private WorkspaceGeneration emptyGeneration() {
        WorkspaceModuleGraph graph = new WorkspaceModuleGraph(
                Collections.<String, WorkspaceModule>emptyMap(),
                Collections.<String, String>emptyMap(),
                Collections.<String>emptyList());
        return new WorkspaceGeneration("context-test", java.nio.file.Paths.get("."),
                ExecutionPolicy.CALLER_THREAD,
                graph, Collections.<String, WorkspaceProgram>emptyMap(), new Nova());
    }
}
