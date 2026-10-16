package com.novalang.runtime.compilation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Compilation 一次加载得到的不可变完整模块图。
 */
public final class ModuleGraph {

    private final Map<String, SourceModule> modules;
    private final String entryModuleId;
    private final List<String> topologicalOrder;

    /**
     * 创建不可变模块图。
     *
     * @param modules 规范模块标识到模块的映射
     * @param entryModuleId 唯一入口的规范模块标识
     * @param topologicalOrder 依赖优先的拓扑顺序
     */
    ModuleGraph(Map<String, SourceModule> modules,
                         String entryModuleId,
                         List<String> topologicalOrder) {
        this.modules = Collections.unmodifiableMap(new LinkedHashMap<String, SourceModule>(modules));
        this.entryModuleId = entryModuleId;
        this.topologicalOrder = Collections.unmodifiableList(new ArrayList<String>(topologicalOrder));
    }

    /** @return 不可变模块映射 */
    public Map<String, SourceModule> getModules() {
        return modules;
    }

    /** @return 唯一入口的规范模块标识 */
    public String getEntryModuleId() {
        return entryModuleId;
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
     * @throws CompilationException 模块不存在时抛出
     */
    public SourceModule requireModule(String moduleId) {
        SourceModule module = modules.get(moduleId);
        if (module == null) {
            throw new CompilationException("Compilation module does not exist: " + moduleId);
        }
        return module;
    }
}
