package com.novalang.runtime.compilation;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
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
 * Nova 文件、路径别名、相对 import 与共享模块的统一解析器。
 *
 * <p>解析器在任何 Nova 程序执行前构建完整依赖图，并把每条字符串 import 重写为
 * Compilation 内唯一模块标识。缺失模块和循环依赖均在加载阶段失败。</p>
 */
public final class ModuleResolver {

    private static final Pattern STRING_IMPORT = Pattern.compile(
            "^(\\s*import\\s+\")((?:\\\\.|[^\"\\\\])*)(\"\\s*;?\\s*(?://.*)?)$");
    private static final Pattern STRING_IMPORT_PREFIX = Pattern.compile("^\\s*import\\s+\"");

    /**
     * 从唯一物理入口递归解析 import，不扫描目录或限制源码根目录。
     * @param entryFile 入口文件，相对路径按宿主工作目录解析
     * @param config 可选编译元数据
     * @return 只包含入口可达模块的不可变依赖图
     */
    public ModuleGraph resolve(Path entryFile, CompilationConfig config) {
        if (entryFile == null || config == null) {
            throw new IllegalArgumentException("Entry file and compilation metadata are required");
        }
        ResolutionContext context = new ResolutionContext(config, null);
        SourceUnit entry = context.loadPhysical(entryFile, "entry " + entryFile);
        context.visit(entry);
        return new ModuleGraph(context.modules, entry.getModuleId(), context.topologicalOrder);
    }

    /**
     * 从唯一宿主源码入口递归解析 import；入口本身无需再登记到元数据中。
     * @param entry 虚拟或宿主生成的入口源码
     * @param config 可选编译元数据
     * @return 只包含入口可达模块的不可变依赖图
     */
    public ModuleGraph resolve(SourceUnit entry, CompilationConfig config) {
        if (entry == null || config == null) {
            throw new IllegalArgumentException("Entry source and compilation metadata are required");
        }
        ResolutionContext context = new ResolutionContext(config, entry);
        context.visit(entry);
        return new ModuleGraph(context.modules, entry.getModuleId(), context.topologicalOrder);
    }

    /**
     * 解析单入口可达的宿主虚拟模块，保留每个模块的行内契约和来源映射。
     */
    public ModuleGraph resolve(SourceUnit entry, CompilationConfig config,
                               java.util.Collection<SourceUnit> sources) {
        if (entry == null || config == null || sources == null) {
            throw new IllegalArgumentException("Entry, metadata and virtual sources are required");
        }
        ResolutionContext context = new ResolutionContext(config, entry);
        for (SourceUnit source : sources) {
            if (source == null || entry.getModuleId().equals(source.getModuleId())
                    || context.sharedSources.putIfAbsent(source.getModuleId(), source) != null) {
                throw new CompilationException("Duplicate or invalid virtual module: "
                        + (source == null ? "null" : source.getModuleId()));
            }
        }
        context.visit(entry);
        return new ModuleGraph(context.modules, entry.getModuleId(), context.topologicalOrder);
    }

    /**
     * 单次模块图构建使用的可变解析上下文。
     */
    private static final class ResolutionContext {

        private final SourceUnit entrySource;
        private final Map<String, SourceUnit> sharedSources = new LinkedHashMap<String, SourceUnit>();
        private final Map<Path, SourceUnit> physicalSources = new HashMap<Path, SourceUnit>();
        private final Map<String, SourceModule> modules = new LinkedHashMap<String, SourceModule>();
        private final List<String> topologicalOrder = new ArrayList<String>();
        private final Map<String, VisitState> states = new HashMap<String, VisitState>();
        private final Deque<String> visitingStack = new ArrayDeque<String>();
        private final Map<String, Path> realAliases = new LinkedHashMap<String, Path>();

        /**
         * 创建一次模块解析过程的可变上下文。
         *
         * @param config 编译元数据
         * @param entrySource 宿主源码入口；物理文件入口使用物理路径缓存识别身份
         */
        ResolutionContext(CompilationConfig config, SourceUnit entrySource) {
            this.entrySource = entrySource;
            realAliases.putAll(config.getAliases());
            if (entrySource != null && sharedSources.containsKey(entrySource.getModuleId())) {
                throw new CompilationException("Entry conflicts with a shared module: " + entrySource.getModuleId());
            }
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
                throw new CompilationException("Cyclic module dependency detected: " + describeCycle(moduleId));
            }

            states.put(moduleId, VisitState.VISITING);
            visitingStack.addLast(moduleId);
            TransformResult transformed = transformImports(source);
            for (SourceUnit dependency : transformed.dependencies) {
                visit(dependency);
            }
            visitingStack.removeLast();
            states.put(moduleId, VisitState.VISITED);
            modules.put(moduleId, new SourceModule(source, transformed.source, transformed.dependencyIds));
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
                        throw new CompilationException("String import must occupy its own line: "
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
         * 按入口身份、共享模块、路径别名或相对路径规则解析单条 import。
         *
         * @param importer 发起导入的源码单元
         * @param specifier import 路径
         * @return 被导入源码单元
         */
        private SourceUnit resolveImport(SourceUnit importer, String specifier) {
            // 回引宿主入口时复用同一身份，由依赖遍历报告循环引用。
            if (entrySource != null && entrySource.getModuleId().equals(specifier)) {
                return entrySource;
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
                    throw new CompilationException("A virtual module without a physical origin cannot use relative imports: "
                            + importer.getModuleId() + " -> " + specifier);
                }
                candidate = origin.getParent().resolve(normalizeSpecifier(specifier));
            } else {
                throw new CompilationException("Compilation string import must use an @ alias or a relative path: "
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
                throw new CompilationException("Undeclared Compilation alias: " + specifier);
            }
            String remainder = specifier.substring(selected.length());
            if (remainder.startsWith("/")) {
                remainder = remainder.substring(1);
            }
            if (remainder.isEmpty()) {
                throw new CompilationException("Alias import must include a module path: " + specifier);
            }
            Path directory = toRealDirectory(realAliases.get(selected), "Alias " + selected);
            return directory.resolve(normalizeSpecifier(remainder));
        }

        /**
         * 加载 UTF-8 Nova 文件并规范化扩展名及真实路径。
         *
         * @param unresolved 尚未解析的文件路径
         * @param description 用于异常信息的导入描述
         * @return 物理源码单元
         */
        private SourceUnit loadPhysical(Path unresolved, String description) {
            Path withExtension = appendNovaExtension(unresolved).toAbsolutePath().normalize();
            if (!Files.isRegularFile(withExtension)) {
                throw new CompilationException("Nova module does not exist: " + description + " -> " + withExtension);
            }

            Path realFile;
            try {
                realFile = withExtension.toRealPath();
            } catch (IOException exception) {
                throw new CompilationException("Failed to resolve Nova module path: " + withExtension, exception);
            }

            SourceUnit cached = physicalSources.get(realFile);
            if (cached != null) {
                return cached;
            }

            String source;
            try {
                source = new String(Files.readAllBytes(realFile), StandardCharsets.UTF_8);
            } catch (IOException exception) {
                throw new CompilationException("Failed to read Nova module: " + realFile, exception);
            }
            String moduleId = buildCanonicalModuleId(realFile);
            if (entrySource != null && entrySource.getModuleId().equals(moduleId)) {
                throw new CompilationException("Physical module conflicts with entry source: " + moduleId);
            }
            SourceUnit unit = SourceUnit.physical(moduleId, source, realFile);
            physicalSources.put(realFile, unit);
            return unit;
        }

        /**
         * 使用真实文件 URI 作为稳定身份，确保不同导入路径只加载同一个模块一次。
         * @param realFile 已解析真实路径的 Nova 文件
         * @return 跨目录唯一的模块标识
         */
        private String buildCanonicalModuleId(Path realFile) {
            return realFile.toUri().toString();
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
                throw new CompilationException(description + " directory does not exist: " + path);
            }
            try {
                return path.toRealPath();
            } catch (IOException exception) {
                throw new CompilationException("Failed to resolve " + description + ": " + path, exception);
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
                throw new CompilationException("Compilation module paths must use '/': " + specifier);
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
