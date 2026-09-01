package com.novalang.workspace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 将完整模块图合并为一次编译输入，同时用对象命名空间隔离各入口的私有模块。
 *
 * <p>被两个以上入口引用的模块只在顶层写入一次。仅属于单个入口的模块写入该入口
 * 的独立 object，因此多个 YAML action 可以继续导出同名 {@code execute}，而公共
 * main/lib 不再随每个入口重复进入编译管线。</p>
 */
final class WorkspaceCompilationBundleBuilder {

    private static final Pattern STRING_IMPORT = Pattern.compile(
            "^\\s*import\\s+\".*\"\\s*;?\\s*(?://.*)?$");
    private static final Pattern JAVA_IMPORT = Pattern.compile(
            "^\\s*import\\s+(?:java|static)\\s+.*$");
    private static final Pattern PACKAGE_DECLARATION = Pattern.compile(
            "^\\s*package\\s+([A-Za-z_][A-Za-z0-9_.]*)\\s*;?\\s*$");

    WorkspaceCompilationBundle build(WorkspaceModuleGraph graph) {
        Map<String, Set<String>> reachableByEntry = collectReachableByEntry(graph);
        Map<String, Integer> usageCounts = countModuleUsage(reachableByEntry);
        String packageName = resolvePackageName(graph);

        StringBuilder source = new StringBuilder();
        List<WorkspaceSourceMap.LineMapping> mappings =
                new ArrayList<WorkspaceSourceMap.LineMapping>();
        Map<String, String> entryObjects = new LinkedHashMap<String, String>();

        appendPackageDeclaration(source, mappings, packageName);
        appendHoistedPrivateImports(graph, usageCounts, source, mappings);
        appendSharedModules(graph, usageCounts, source, mappings);

        int objectIndex = 0;
        for (Map.Entry<String, String> entry : graph.getEntries().entrySet()) {
            String entryName = entry.getKey();
            String moduleId = entry.getValue();
            Integer usage = usageCounts.get(moduleId);
            if (usage != null && usage.intValue() > 1) {
                entryObjects.put(entryName, null);
                continue;
            }

            String objectName = "__WorkspaceEntry" + objectIndex;
            objectIndex++;
            String objectClassName = packageName == null
                    ? objectName : packageName + "." + objectName;
            entryObjects.put(entryName, objectClassName);
            appendLine(source, mappings, "object " + objectName + " {", null, 0);

            Set<String> reachable = reachableByEntry.get(entryName);
            for (String reachableModuleId : graph.getTopologicalOrder()) {
                Integer reachableUsage = usageCounts.get(reachableModuleId);
                if (reachable == null || !reachable.contains(reachableModuleId)
                        || reachableUsage == null || reachableUsage.intValue() != 1) {
                    continue;
                }
                WorkspaceModule module = graph.requireModule(reachableModuleId);
                appendLine(source, mappings, "    // module: " + reachableModuleId, null, 0);
                appendModule(source, mappings, module, true);
            }
            appendLine(source, mappings, "}", null, 0);
        }

        if (source.length() > 0) {
            source.setLength(source.length() - 1);
        }
        return new WorkspaceCompilationBundle(source.toString(),
                new WorkspaceSourceMap(mappings), entryObjects);
    }

    private Map<String, Set<String>> collectReachableByEntry(WorkspaceModuleGraph graph) {
        Map<String, Set<String>> result = new LinkedHashMap<String, Set<String>>();
        for (Map.Entry<String, String> entry : graph.getEntries().entrySet()) {
            Set<String> reachable = new LinkedHashSet<String>();
            collectReachable(graph, entry.getValue(), reachable);
            result.put(entry.getKey(), reachable);
        }
        return result;
    }

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

    private Map<String, Integer> countModuleUsage(Map<String, Set<String>> reachableByEntry) {
        Map<String, Integer> counts = new LinkedHashMap<String, Integer>();
        for (Set<String> reachable : reachableByEntry.values()) {
            for (String moduleId : reachable) {
                Integer count = counts.get(moduleId);
                if (count == null) {
                    counts.put(moduleId, Integer.valueOf(1));
                } else {
                    counts.put(moduleId, Integer.valueOf(count.intValue() + 1));
                }
            }
        }
        return counts;
    }

    private String resolvePackageName(WorkspaceModuleGraph graph) {
        String packageName = null;
        for (WorkspaceModule module : graph.getModules().values()) {
            String[] lines = module.getTransformedSource().split("\\r?\\n", -1);
            for (String line : lines) {
                Matcher matcher = PACKAGE_DECLARATION.matcher(line);
                if (!matcher.matches()) {
                    continue;
                }
                String current = matcher.group(1);
                if (packageName == null) {
                    packageName = current;
                } else if (!packageName.equals(current)) {
                    throw new WorkspaceException(
                            "Workspace modules must use one package in a joint compilation: "
                                    + packageName + " and " + current);
                }
            }
        }
        return packageName;
    }

    private void appendPackageDeclaration(StringBuilder source,
                                          List<WorkspaceSourceMap.LineMapping> mappings,
                                          String packageName) {
        if (packageName != null) {
            appendLine(source, mappings, "package " + packageName, null, 0);
        }
    }

    private void appendHoistedPrivateImports(WorkspaceModuleGraph graph,
                                             Map<String, Integer> usageCounts,
                                             StringBuilder source,
                                             List<WorkspaceSourceMap.LineMapping> mappings) {
        Set<String> imports = new HashSet<String>();
        for (String moduleId : graph.getTopologicalOrder()) {
            Integer usage = usageCounts.get(moduleId);
            if (usage == null || usage.intValue() != 1) {
                continue;
            }
            WorkspaceModule module = graph.requireModule(moduleId);
            String[] lines = module.getTransformedSource().split("\\r?\\n", -1);
            for (int index = 0; index < lines.length; index++) {
                String line = lines[index];
                if (JAVA_IMPORT.matcher(line).matches() && imports.add(line.trim())) {
                    appendLine(source, mappings, line,
                            module.getSourceUnit(), index + 1);
                }
            }
        }
    }

    private void appendSharedModules(WorkspaceModuleGraph graph,
                                     Map<String, Integer> usageCounts,
                                     StringBuilder source,
                                     List<WorkspaceSourceMap.LineMapping> mappings) {
        for (String moduleId : graph.getTopologicalOrder()) {
            Integer usage = usageCounts.get(moduleId);
            if (usage == null || usage.intValue() <= 1) {
                continue;
            }
            WorkspaceModule module = graph.requireModule(moduleId);
            appendLine(source, mappings, "// module: " + moduleId, null, 0);
            appendModule(source, mappings, module, false);
        }
    }

    private void appendModule(StringBuilder source,
                              List<WorkspaceSourceMap.LineMapping> mappings,
                              WorkspaceModule module,
                              boolean indent) {
        String[] lines = module.getTransformedSource().split("\\r?\\n", -1);
        for (int index = 0; index < lines.length; index++) {
            String line = lines[index];
            if (STRING_IMPORT.matcher(line).matches()
                    || PACKAGE_DECLARATION.matcher(line).matches()) {
                continue;
            }
            if (indent && JAVA_IMPORT.matcher(line).matches()) {
                continue;
            }
            String output = indent ? "    " + line : line;
            appendLine(source, mappings, output, module.getSourceUnit(), index + 1);
        }
    }

    private void appendLine(StringBuilder source,
                            List<WorkspaceSourceMap.LineMapping> mappings,
                            String line,
                            SourceUnit sourceUnit,
                            int moduleLine) {
        source.append(line).append('\n');
        mappings.add(new WorkspaceSourceMap.LineMapping(sourceUnit, moduleLine));
    }
}
