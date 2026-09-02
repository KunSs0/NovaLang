package com.novalang.workspace;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 将一个模块编译组构造成独立且可链接的 Nova 编译单元。
 */
final class WorkspaceCompilationGroupBuilder {

    private static final Pattern STRING_IMPORT = Pattern.compile(
            "^\\s*import\\s+\".*\"\\s*;?\\s*(?://.*)?$");
    private static final Pattern JAVA_IMPORT = Pattern.compile(
            "^\\s*import\\s+(?:java|static)\\s+.*$");
    private static final Pattern PACKAGE_DECLARATION = Pattern.compile(
            "^\\s*package\\s+[A-Za-z_$][A-Za-z0-9_$.]*\\s*;?\\s*$");

    WorkspaceBundle build(WorkspaceModuleGraph graph,
                          WorkspaceCompilationPlan.Group group,
                          Map<String, WorkspaceCompilationExports> exportsByGroup) {
        StringBuilder source = new StringBuilder();
        List<WorkspaceSourceMap.LineMapping> mappings =
                new ArrayList<WorkspaceSourceMap.LineMapping>();
        appendLine(source, mappings, "package " + group.getPackageName(), null, 0);

        Set<String> imports = new LinkedHashSet<String>();
        appendLinkImports(group, exportsByGroup, imports, source, mappings);
        appendJavaImports(graph, group, imports, source, mappings);

        for (String moduleId : group.getModuleIds()) {
            WorkspaceModule module = graph.requireModule(moduleId);
            appendLine(source, mappings, "// module: " + moduleId, null, 0);
            String[] lines = module.getTransformedSource().split("\\r?\\n", -1);
            for (int index = 0; index < lines.length; index++) {
                String line = lines[index];
                if (STRING_IMPORT.matcher(line).matches()
                        || JAVA_IMPORT.matcher(line).matches()
                        || PACKAGE_DECLARATION.matcher(line).matches()) {
                    continue;
                }
                appendLine(source, mappings, line, module.getSourceUnit(), index + 1);
            }
        }

        if (source.length() > 0) {
            source.setLength(source.length() - 1);
        }
        return new WorkspaceBundle(source.toString(), new WorkspaceSourceMap(mappings));
    }

    private void appendLinkImports(WorkspaceCompilationPlan.Group group,
                                   Map<String, WorkspaceCompilationExports> exportsByGroup,
                                   Set<String> imports,
                                   StringBuilder source,
                                   List<WorkspaceSourceMap.LineMapping> mappings) {
        for (WorkspaceCompilationPlan.Group dependency : group.getDependencies()) {
            WorkspaceCompilationExports exports = exportsByGroup.get(dependency.getId());
            if (exports == null) {
                throw new WorkspaceException(
                        "Workspace dependency group is not compiled: " + dependency.getId());
            }
            for (String memberName : exports.getStaticMemberNames()) {
                appendImport("import static " + dependency.getPackageName()
                                + ".$Module." + memberName,
                        imports, source, mappings, null, 0);
            }
            for (String objectName : exports.getObjectNames()) {
                appendImport("import static " + dependency.getPackageName() + "."
                                + objectName + ".INSTANCE as " + objectName,
                        imports, source, mappings, null, 0);
            }
            for (String typeName : exports.getTypeNames()) {
                appendImport("import java " + dependency.getPackageName() + "." + typeName,
                        imports, source, mappings, null, 0);
            }
        }
    }

    private void appendJavaImports(WorkspaceModuleGraph graph,
                                   WorkspaceCompilationPlan.Group group,
                                   Set<String> imports,
                                   StringBuilder source,
                                   List<WorkspaceSourceMap.LineMapping> mappings) {
        for (String moduleId : group.getModuleIds()) {
            WorkspaceModule module = graph.requireModule(moduleId);
            String[] lines = module.getTransformedSource().split("\\r?\\n", -1);
            for (int index = 0; index < lines.length; index++) {
                String line = lines[index];
                if (JAVA_IMPORT.matcher(line).matches()) {
                    appendImport(line.trim(), imports, source, mappings,
                            module.getSourceUnit(), index + 1);
                }
            }
        }
    }

    private void appendImport(String line,
                              Set<String> imports,
                              StringBuilder source,
                              List<WorkspaceSourceMap.LineMapping> mappings,
                              SourceUnit sourceUnit,
                              int sourceLine) {
        if (imports.add(line)) {
            appendLine(source, mappings, line, sourceUnit, sourceLine);
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
