package com.novalang.workspace;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 整个 Workspace 模块图的一次联合编译输入。
 */
final class WorkspaceCompilationBundle {

    private final String source;
    private final WorkspaceSourceMap sourceMap;
    private final Map<String, String> entryObjectClasses;

    /**
     * @param source 每个模块只出现一次的联合源码
     * @param sourceMap 联合源码到原始模块的逐行映射
     * @param entryObjectClasses 入口名称到私有入口对象类名；值为空表示入口位于顶层
     */
    WorkspaceCompilationBundle(String source,
                               WorkspaceSourceMap sourceMap,
                               Map<String, String> entryObjectClasses) {
        this.source = source;
        this.sourceMap = sourceMap;
        this.entryObjectClasses = Collections.unmodifiableMap(
                new LinkedHashMap<String, String>(entryObjectClasses));
    }

    String getSource() {
        return source;
    }

    WorkspaceSourceMap getSourceMap() {
        return sourceMap;
    }

    String getEntryObjectClass(String entryName) {
        return entryObjectClasses.get(entryName);
    }
}
