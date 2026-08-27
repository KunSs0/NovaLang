package com.novalang.workspace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 将一个入口的可达模块按依赖优先顺序合并为无 import 编译单元。
 */
final class WorkspaceBundleBuilder {

    private static final Pattern STRING_IMPORT = Pattern.compile("^\\s*import\\s+\".*\"\\s*;?\\s*(?://.*)?$");

    /**
     * 构建指定入口的完整编译源码和逐行映射。
     *
     * @param graph 完整 Workspace 模块图
     * @param entryModuleId 入口规范模块标识
     * @return 入口编译包
     */
    WorkspaceBundle build(WorkspaceModuleGraph graph, String entryModuleId) {
        Set<String> reachable = new HashSet<String>();
        collectReachable(graph, entryModuleId, reachable);

        StringBuilder source = new StringBuilder();
        List<WorkspaceSourceMap.LineMapping> mappings =
                new ArrayList<WorkspaceSourceMap.LineMapping>();
        for (String moduleId : graph.getTopologicalOrder()) {
            if (!reachable.contains(moduleId)) {
                continue;
            }
            WorkspaceModule module = graph.requireModule(moduleId);

            // 每个模块前插入无来源注释，便于查看生成源码，同时避免影响业务行映射。
            appendLine(source, mappings, "// module: " + moduleId, null, 0);
            String[] lines = module.getTransformedSource().split("\\r?\\n", -1);
            for (int index = 0; index < lines.length; index++) {
                if (STRING_IMPORT.matcher(lines[index]).matches()) {
                    // 依赖已按拓扑顺序合并，原 import 行不再交给 Nova 二次解析。
                    continue;
                }
                appendLine(source, mappings, lines[index], module.getSourceUnit(), index + 1);
            }
        }

        if (source.length() > 0) {
            source.setLength(source.length() - 1);
        }
        return new WorkspaceBundle(source.toString(), new WorkspaceSourceMap(mappings));
    }

    /**
     * 递归收集入口能够到达的全部模块。
     *
     * @param graph 完整模块图
     * @param moduleId 当前模块标识
     * @param reachable 已收集模块集合
     */
    private void collectReachable(WorkspaceModuleGraph graph,
                                  String moduleId,
                                  Set<String> reachable) {
        if (!reachable.add(moduleId)) {
            return;
        }
        WorkspaceModule module = graph.requireModule(moduleId);
        for (String dependency : module.getDependencies()) {
            collectReachable(graph, dependency, reachable);
        }
    }

    /**
     * 向合并源码追加一行，并同步写入相同下标的来源记录。
     *
     * @param source 合并源码缓冲区
     * @param mappings 逐行来源记录
     * @param line 待追加文本
     * @param sourceUnit 原始源码单元；生成注释为 {@code null}
     * @param moduleLine 模块内行号
     */
    private void appendLine(StringBuilder source,
                            List<WorkspaceSourceMap.LineMapping> mappings,
                            String line,
                            SourceUnit sourceUnit,
                            int moduleLine) {
        source.append(line).append('\n');
        mappings.add(new WorkspaceSourceMap.LineMapping(sourceUnit, moduleLine));
    }
}
