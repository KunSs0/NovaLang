package com.novalang.workspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Workspace 一次加载得到的不可变完整模块图。
 */
public final class WorkspaceModuleGraph {

    private final Map<String, WorkspaceModule> modules;
    private final Map<String, String> entries;
    private final List<String> topologicalOrder;

    /**
     * 创建不可变模块图。
     *
     * @param modules 规范模块标识到模块的映射
     * @param entries 配置入口名称到规范模块标识的映射
     * @param topologicalOrder 依赖优先的拓扑顺序
     */
    WorkspaceModuleGraph(Map<String, WorkspaceModule> modules,
                         Map<String, String> entries,
                         List<String> topologicalOrder) {
        this.modules = Collections.unmodifiableMap(new LinkedHashMap<String, WorkspaceModule>(modules));
        this.entries = Collections.unmodifiableMap(new LinkedHashMap<String, String>(entries));
        this.topologicalOrder = Collections.unmodifiableList(new ArrayList<String>(topologicalOrder));
    }

    /** @return 不可变模块映射 */
    public Map<String, WorkspaceModule> getModules() {
        return modules;
    }

    /** @return 配置入口到规范模块标识的映射 */
    public Map<String, String> getEntries() {
        return entries;
    }

    /** @return 依赖优先的不可变拓扑顺序 */
    public List<String> getTopologicalOrder() {
        return topologicalOrder;
    }

    /**
     * 获取指定规范标识的模块。
     *
     * @param moduleId 规范模块标识
     * @return 已解析模块
     * @throws WorkspaceException 模块不存在时抛出
     */
    public WorkspaceModule requireModule(String moduleId) {
        WorkspaceModule module = modules.get(moduleId);
        if (module == null) {
            throw new WorkspaceException("Workspace module does not exist: " + moduleId);
        }
        return module;
    }
}
