package com.novalang.workspace;

import com.novalang.runtime.interpreter.ModuleLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Workspace 文件、Alias、相对 import 与虚拟源码的统一模块解析器。
 *
 * <p>解析器在任何 Nova 程序执行前构建完整依赖图，并把每条字符串 import 重写为
 * Workspace 内唯一模块标识。文件越界、缺失模块和循环依赖均在加载阶段失败。</p>
 */
public final class WorkspaceModuleResolver {

    private static final Pattern STRING_IMPORT = Pattern.compile(
            "^(\\s*import\\s+\")((?:\\\\.|[^\"\\\\])*)(\"\\s*;?\\s*(?://.*)?)$");
    private static final Pattern STRING_IMPORT_PREFIX = Pattern.compile("^\\s*import\\s+\"");

    /**
     * 仅根据配置中的物理入口构建模块图。
     *
     * @param config Workspace 配置
     * @return 完整不可变模块图
     */
    public WorkspaceModuleGraph resolve(WorkspaceConfig config) {
        return resolve(config, Collections.<SourceUnit>emptyList(), Collections.<String>emptyList());
    }

    /**
     * 根据配置入口及业务生成的虚拟入口构建模块图。
     *
     * @param config Workspace 配置
     * @param virtualSources YAML 等业务适配器生成的虚拟源码
     * @param virtualEntries 需要作为入口编译的虚拟模块标识
     * @return 完整不可变模块图
     */
    public WorkspaceModuleGraph resolve(WorkspaceConfig config,
                                        Collection<SourceUnit> virtualSources,
                                        Collection<String> virtualEntries) {
        if (config == null) {
            throw new WorkspaceException("WorkspaceConfig must not be null");
        }
        if (virtualSources == null || virtualEntries == null) {
            throw new WorkspaceException("Virtual sources and entries must not be null");
        }

        ResolutionContext context = new ResolutionContext(config, virtualSources);
        context.validatePaths();

        Map<String, String> entries = new LinkedHashMap<String, String>();
        for (String entry : config.getEntries()) {
            SourceUnit source = context.resolveEntry(entry);
            context.visit(source);
            entries.put(entry, source.getModuleId());
        }
        for (String virtualEntry : virtualEntries) {
            SourceUnit source = context.virtualSources.get(virtualEntry);
            if (source == null) {
                throw new WorkspaceException("Virtual entry source is not registered: " + virtualEntry);
            }
            context.visit(source);
            if (entries.put(virtualEntry, source.getModuleId()) != null) {
                throw new WorkspaceException("Duplicate entry name: " + virtualEntry);
            }
        }

        return new WorkspaceModuleGraph(context.modules, entries, context.topologicalOrder);
    }

    /**
     * 单次模块图构建使用的可变解析上下文。
     */
    private static final class ResolutionContext {

        private final WorkspaceConfig config;
        private final Map<String, SourceUnit> virtualSources = new LinkedHashMap<String, SourceUnit>();
        private final Map<String, SourceUnit> sharedSources = new LinkedHashMap<String, SourceUnit>();
        private final Map<Path, SourceUnit> physicalSources = new HashMap<Path, SourceUnit>();
        private final Map<String, WorkspaceModule> modules = new LinkedHashMap<String, WorkspaceModule>();
        private final List<String> topologicalOrder = new ArrayList<String>();
        private final Map<String, VisitState> states = new HashMap<String, VisitState>();
        private final Deque<String> visitingStack = new ArrayDeque<String>();
        private final List<Path> realSourceRoots = new ArrayList<Path>();
        private final Map<String, Path> realAliases = new LinkedHashMap<String, Path>();

        /**
         * 创建一次模块解析过程的可变上下文。
         *
         * @param config Workspace 配置
         * @param virtualSources 业务预注册虚拟源码
         */
        ResolutionContext(WorkspaceConfig config, Collection<SourceUnit> virtualSources) {
            this.config = config;
            Map<String, String> sharedModules = ModuleLoader.sharedModuleSnapshot();
            for (Map.Entry<String, String> entry : sharedModules.entrySet()) {
                SourceUnit source = new SourceUnit(
                        entry.getKey(), entry.getValue(), null, null, 1, 0, null);
                sharedSources.put(entry.getKey(), source);
            }
            for (SourceUnit source : virtualSources) {
                if (sharedSources.containsKey(source.getModuleId())) {
                    throw new WorkspaceException(
                            "Shared and Workspace virtual module IDs conflict: " + source.getModuleId());
                }
                SourceUnit previous = this.virtualSources.put(source.getModuleId(), source);
                if (previous != null) {
                    throw new WorkspaceException("Duplicate virtual module ID: " + source.getModuleId());
                }
            }
        }

        /**
         * 将源码根与 Alias 目标解析为真实目录并执行越界校验。
         */
        void validatePaths() {
            for (Path configuredRoot : config.getSourceRoots()) {
                Path realRoot = toRealDirectory(configuredRoot, "source root");
                if (!realSourceRoots.contains(realRoot)) {
                    realSourceRoots.add(realRoot);
                }
            }

            for (Map.Entry<String, Path> alias : config.getAliases().entrySet()) {
                Path realTarget = toRealDirectory(alias.getValue(), "Alias " + alias.getKey());
                requireInsideSourceRoots(realTarget, "Alias " + alias.getKey());
                realAliases.put(alias.getKey(), realTarget);
            }
        }

        /**
         * 解析配置入口或同名虚拟入口。
         *
         * @param entry 入口声明
         * @return 入口源码单元
         */
        SourceUnit resolveEntry(String entry) {
            SourceUnit virtual = virtualSources.get(entry);
            if (virtual != null) {
                return virtual;
            }

            Path candidate;
            if (entry.startsWith("@")) {
                candidate = resolveAlias(entry);
            } else {
                candidate = config.getRootDirectory().resolve(normalizeSpecifier(entry));
            }
            return loadPhysical(candidate, "entry " + entry);
        }

        /**
         * 深度优先解析当前模块依赖并写入拓扑顺序。
         *
         * @param source 当前源码单元
         */
        void visit(SourceUnit source) {
            String moduleId = source.getModuleId();
            VisitState state = states.get(moduleId);
            if (state == VisitState.VISITED) {
                return;
            }
            if (state == VisitState.VISITING) {
                throw new WorkspaceException("Cyclic module dependency detected: " + describeCycle(moduleId));
            }

            states.put(moduleId, VisitState.VISITING);
            visitingStack.addLast(moduleId);
            TransformResult transformed = transformImports(source);
            for (SourceUnit dependency : transformed.dependencies) {
                visit(dependency);
            }
            visitingStack.removeLast();
            states.put(moduleId, VisitState.VISITED);
            modules.put(moduleId, new WorkspaceModule(source, transformed.source, transformed.dependencyIds));
            topologicalOrder.add(moduleId);
        }

        /**
         * 解析独占行字符串 import，并改写为规范模块标识。
         *
         * @param source 待处理源码单元
         * @return 改写源码及直接依赖
         */
        private TransformResult transformImports(SourceUnit source) {
            String[] lines = source.getSourceText().split("\\r?\\n", -1);
            StringBuilder transformed = new StringBuilder(source.getSourceText().length() + 64);
            List<SourceUnit> dependencies = new ArrayList<SourceUnit>();
            Set<String> dependencyIds = new LinkedHashSet<String>();

            for (int index = 0; index < lines.length; index++) {
                String line = lines[index];
                Matcher matcher = STRING_IMPORT.matcher(line);
                if (matcher.matches()) {
                    String specifier = unescape(matcher.group(2));
                    SourceUnit dependency = resolveImport(source, specifier);
                    if (dependencyIds.add(dependency.getModuleId())) {
                        dependencies.add(dependency);
                    }
                    transformed.append(matcher.group(1));
                    transformed.append(escape(dependency.getModuleId()));
                    transformed.append(matcher.group(3));
                } else {
                    if (STRING_IMPORT_PREFIX.matcher(line).find()) {
                        throw new WorkspaceException("String import must occupy its own line: "
                                + source.describeOrigin(index + 1));
                    }
                    transformed.append(line);
                }
                if (index < lines.length - 1) {
                    transformed.append('\n');
                }
            }

            return new TransformResult(transformed.toString(), dependencies,
                    new ArrayList<String>(dependencyIds));
        }

        /**
         * 按虚拟模块、Alias 或相对路径规则解析单条 import。
         *
         * @param importer 发起导入的源码单元
         * @param specifier import 路径
         * @return 被导入源码单元
         */
        private SourceUnit resolveImport(SourceUnit importer, String specifier) {
            SourceUnit virtual = virtualSources.get(specifier);
            if (virtual != null) {
                return virtual;
            }
            SourceUnit shared = sharedSources.get(specifier);
            if (shared != null) {
                return shared;
            }

            Path candidate;
            if (specifier.startsWith("@")) {
                candidate = resolveAlias(specifier);
            } else if (specifier.startsWith("./") || specifier.startsWith("../")) {
                Path origin = importer.getOriginFile();
                if (origin == null || origin.getParent() == null) {
                    throw new WorkspaceException("A virtual module without a physical origin cannot use relative imports: "
                            + importer.getModuleId() + " -> " + specifier);
                }
                candidate = origin.getParent().resolve(normalizeSpecifier(specifier));
            } else {
                throw new WorkspaceException("Workspace string import must use an @ alias or a relative path: "
                        + importer.getModuleId() + " -> " + specifier);
            }
            return loadPhysical(candidate, importer.getModuleId() + " -> " + specifier);
        }

        /**
         * 使用边界匹配和最长 Alias 规则翻译导入路径。
         *
         * @param specifier 以 {@code @} 开头的模块路径
         * @return 尚未执行真实路径校验的物理候选路径
         */
        private Path resolveAlias(String specifier) {
            String selected = null;
            for (String alias : realAliases.keySet()) {
                boolean exact = specifier.equals(alias);
                boolean child = specifier.startsWith(alias + "/");
                if (exact || child) {
                    if (selected == null || alias.length() > selected.length()) {
                        selected = alias;
                    }
                }
            }
            if (selected == null) {
                throw new WorkspaceException("Undeclared Workspace alias: " + specifier);
            }
            String remainder = specifier.substring(selected.length());
            if (remainder.startsWith("/")) {
                remainder = remainder.substring(1);
            }
            if (remainder.isEmpty()) {
                throw new WorkspaceException("Alias import must include a module path: " + specifier);
            }
            return realAliases.get(selected).resolve(normalizeSpecifier(remainder));
        }

        /**
         * 加载 UTF-8 Nova 文件并执行扩展名、真实路径及源码根校验。
         *
         * @param unresolved 尚未解析的文件路径
         * @param description 用于异常信息的导入描述
         * @return 物理源码单元
         */
        private SourceUnit loadPhysical(Path unresolved, String description) {
            Path withExtension = appendNovaExtension(unresolved).toAbsolutePath().normalize();
            if (!Files.isRegularFile(withExtension)) {
                throw new WorkspaceException("Nova module does not exist: " + description + " -> " + withExtension);
            }

            Path realFile;
            try {
                realFile = withExtension.toRealPath();
            } catch (IOException exception) {
                throw new WorkspaceException("Failed to resolve Nova module path: " + withExtension, exception);
            }
            requireInsideSourceRoots(realFile, description);

            SourceUnit cached = physicalSources.get(realFile);
            if (cached != null) {
                return cached;
            }

            String source;
            try {
                source = new String(Files.readAllBytes(realFile), StandardCharsets.UTF_8);
            } catch (IOException exception) {
                throw new WorkspaceException("Failed to read Nova module: " + realFile, exception);
            }
            String moduleId = buildCanonicalModuleId(realFile);
            if (virtualSources.containsKey(moduleId)) {
                throw new WorkspaceException("Physical and virtual module IDs conflict: " + moduleId);
            }
            SourceUnit unit = SourceUnit.physical(moduleId, source, realFile);
            physicalSources.put(realFile, unit);
            return unit;
        }

        /**
         * 按包含目标文件的最深源码根生成稳定模块标识。
         *
         * @param realFile 已解析真实路径的 Nova 文件
         * @return Workspace 内规范模块标识
         */
        private String buildCanonicalModuleId(Path realFile) {
            int selectedIndex = -1;
            int selectedLength = -1;
            for (int index = 0; index < realSourceRoots.size(); index++) {
                Path root = realSourceRoots.get(index);
                if (realFile.startsWith(root) && root.getNameCount() > selectedLength) {
                    selectedIndex = index;
                    selectedLength = root.getNameCount();
                }
            }
            if (selectedIndex < 0) {
                throw new WorkspaceException("Module is outside all source roots: " + realFile);
            }
            Path relative = realSourceRoots.get(selectedIndex).relativize(realFile);
            return "@workspace/source-" + selectedIndex + "/"
                    + relative.toString().replace('\\', '/');
        }

        /**
         * 校验真实路径位于任一已声明源码根内。
         *
         * @param path 待校验真实路径
         * @param description 用于异常信息的路径描述
         */
        private void requireInsideSourceRoots(Path path, String description) {
            for (Path root : realSourceRoots) {
                if (path.startsWith(root)) {
                    return;
                }
            }
            throw new WorkspaceException("Path escapes the declared source roots: " + description + " -> " + path);
        }

        /**
         * 从当前 DFS 栈构造确定性的循环依赖链。
         *
         * @param repeatedModule 再次进入的模块标识
         * @return 使用箭头连接的循环链
         */
        private String describeCycle(String repeatedModule) {
            List<String> cycle = new ArrayList<String>();
            boolean collecting = false;
            for (String module : visitingStack) {
                if (module.equals(repeatedModule)) {
                    collecting = true;
                }
                if (collecting) {
                    cycle.add(module);
                }
            }
            cycle.add(repeatedModule);
            return String.join(" -> ", cycle);
        }

        /**
         * 校验目录存在并解析其真实路径。
         *
         * @param path 配置目录路径
         * @param description 用于异常信息的目录描述
         * @return 真实目录路径
         */
        private static Path toRealDirectory(Path path, String description) {
            if (!Files.isDirectory(path)) {
                throw new WorkspaceException(description + " directory does not exist: " + path);
            }
            try {
                return path.toRealPath();
            } catch (IOException exception) {
                throw new WorkspaceException("Failed to resolve " + description + ": " + path, exception);
            }
        }

        /**
         * 为无扩展名模块路径固定追加 {@code .nova}。
         *
         * @param path 原模块路径
         * @return 带 Nova 扩展名的路径
         */
        private static Path appendNovaExtension(Path path) {
            String fileName = path.getFileName() == null ? "" : path.getFileName().toString();
            if (fileName.endsWith(".nova")) {
                return path;
            }
            if (fileName.isEmpty()) {
                return path.resolve(".nova");
            }
            return path.resolveSibling(fileName + ".nova");
        }

        /**
         * 校验模块路径只使用正斜杠。
         *
         * @param specifier 模块路径
         * @return 原模块路径
         */
        private static String normalizeSpecifier(String specifier) {
            if (specifier.indexOf('\\') >= 0) {
                throw new WorkspaceException("Workspace module paths must use '/': " + specifier);
            }
            return specifier;
        }

        /**
         * 还原字符串 import 中允许的引号和反斜杠转义。
         *
         * @param text 转义文本
         * @return 还原文本
         */
        private static String unescape(String text) {
            return text.replace("\\\"", "\"").replace("\\\\", "\\");
        }

        /**
         * 将规范模块标识转义为 Nova 字符串字面量内容。
         *
         * @param text 原模块标识
         * @return 转义文本
         */
        private static String escape(String text) {
            return text.replace("\\", "\\\\").replace("\"", "\\\"");
        }
    }

    /**
     * 深度优先搜索状态。
     */
    private enum VisitState {
        VISITING,
        VISITED
    }

    /**
     * 单个源码单元的 import 重写结果。
     */
    private static final class TransformResult {
        private final String source;
        private final List<SourceUnit> dependencies;
        private final List<String> dependencyIds;

        /**
         * 创建 import 重写结果。
         *
         * @param source 改写后的源码
         * @param dependencies 直接依赖源码
         * @param dependencyIds 直接依赖模块标识
         */
        TransformResult(String source, List<SourceUnit> dependencies, List<String> dependencyIds) {
            this.source = source;
            this.dependencies = dependencies;
            this.dependencyIds = dependencyIds;
        }
    }
}
