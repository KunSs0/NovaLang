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

    /**
     * 合并本组源码及依赖的原始扩展链接，并保留来源映射。
     * @param graph 已解析模块图。
     * @param group 当前编译组。
     * @param exportsByGroup 已完成编译的依赖导出。
     * @return 可独立编译的源码包。
     */
    WorkspaceBundle build(WorkspaceModuleGraph graph,
                          WorkspaceCompilationPlan.Group group,
                          Map<String, WorkspaceCompilationExports> exportsByGroup) {
        List<WorkspaceBundle.InlinePart> inlineParts = new ArrayList<WorkspaceBundle.InlinePart>();
        StringBuilder source = new StringBuilder();
        List<WorkspaceSourceMap.LineMapping> mappings =
                new ArrayList<WorkspaceSourceMap.LineMapping>();
        appendLine(source, mappings, "package " + group.getPackageName(), null, 0);

        Set<String> imports = new LinkedHashSet<String>();
        appendLinkImports(group, exportsByGroup, imports, source, mappings);
        appendJavaImports(graph, group, imports, source, mappings);
        // 同一个原始扩展可能经多个依赖到达本组，按原始链接身份只注入一次。
        Set<String> inheritedExtensions = new LinkedHashSet<String>();
        for (WorkspaceCompilationPlan.Group dependency : group.getDependencies()) {
            for (String declaration : exportsByGroup.get(dependency.getId()).getExtensionDeclarations()) {
                if (inheritedExtensions.add(declaration)) {
                    for (String line : declaration.split("\n")) {
                        appendLine(source, mappings, line, null, 0);
                    }
                }
            }
        }

        for (String moduleId : group.getModuleIds()) {
            WorkspaceModule module = graph.requireModule(moduleId);
            appendLine(source, mappings, "// module: " + moduleId, null, 0);
            SourceUnit unit = module.getSourceUnit();
            if (unit.isInline()) {
                appendLine(source, mappings, "// inline: " + unit.getInlineEntryName(), unit, 1);
            }
            int startLine = mappings.size();
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
            if (unit.isInline()) {
                inlineParts.add(new WorkspaceBundle.InlinePart(unit, startLine, mappings.size()));
            }
        }

        if (source.length() > 0) {
            source.setLength(source.length() - 1);
        }
        return new WorkspaceBundle(source.toString(), new WorkspaceSourceMap(mappings), inlineParts, inheritedExtensions);
    }

    /**
     * 导入依赖的公开符号，排除仅用于链接的扩展转发函数。
     * @param group 当前编译组。
     * @param exportsByGroup 依赖导出。
     * @param imports 已输出的导入集合。
     * @param source 合并源码。
     * @param mappings 来源映射。
     */
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
            for (String importDeclaration : exports.getJavaImportDeclarations()) {
                appendImport(importDeclaration, imports, source, mappings, null, 0);
            }
            for (String memberName : exports.getStaticMemberNames()) {
                if (exports.getForwardedExtensionNames().contains(memberName)) {
                    continue;
                }
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

    Set<String> collectJavaImportDeclarations(WorkspaceModuleGraph graph,
                                              WorkspaceCompilationPlan.Group group) {
        Set<String> imports = new LinkedHashSet<String>();
        for (String moduleId : group.getModuleIds()) {
            WorkspaceModule module = graph.requireModule(moduleId);
            String[] lines = module.getTransformedSource().split("\\r?\\n", -1);
            for (String line : lines) {
                if (JAVA_IMPORT.matcher(line).matches()) {
                    imports.add(line.trim());
                }
            }
        }
        return imports;
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
