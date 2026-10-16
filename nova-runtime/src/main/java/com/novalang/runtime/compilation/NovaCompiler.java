package com.novalang.runtime.compilation;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 将模块图和编译元数据直接编译为一个 Nova 程序，不创建额外的执行容器。 */
public final class NovaCompiler {
    /**
     * 从物理入口编译完整依赖图，不执行程序初始化。
     * @param nova 提供绑定和 Java 类型的编译门面
     * @param entryFile 唯一入口文件
     * @param config 可选编译元数据
     * @param cache 仅缓存字节码的编译缓存
     * @return 类与静态状态独立的程序
     */
    public CompiledNova compile(Nova nova, Path entryFile, CompilationConfig config, BytecodeArtifactCache cache) {
        return compileGraph(nova, new ModuleResolver().resolve(entryFile, config), config, cache);
    }

    /**
     * 从宿主提供的唯一源码入口编译完整依赖图。
     * @param nova 提供绑定和 Java 类型的编译门面
     * @param entry 唯一源码入口
     * @param config 可选编译元数据
     * @param cache 仅缓存字节码的编译缓存
     * @return 类与静态状态独立的程序
     */
    public CompiledNova compile(Nova nova, SourceUnit entry, CompilationConfig config, BytecodeArtifactCache cache) {
        return compileGraph(nova, new ModuleResolver().resolve(entry, config), config, cache);
    }

    /**
     * 编译包含显式宿主虚拟模块的单入口图，不执行不可达的源码。
     */
    public CompiledNova compile(Nova nova, SourceUnit entry, CompilationConfig config,
                                java.util.Collection<SourceUnit> sources, BytecodeArtifactCache cache) {
        return compileGraph(nova, new ModuleResolver().resolve(entry, config, sources), config, cache);
    }

    /**
     * 按依赖图链接字节码并建立一个程序的函数和初始化索引。
     * @param nova 编译门面
     * @param graph 单入口依赖图
     * @param config 编译元数据
     * @param cache 字节码缓存
     * @return 尚未初始化的独立程序
     */
    private CompiledNova compileGraph(Nova nova, ModuleGraph graph, CompilationConfig config,
                                      BytecodeArtifactCache cache) {
        if (nova == null || cache == null) {
            throw new IllegalArgumentException("Nova and compilation cache are required");
        }
        String identity = graph.getEntryModuleId() + "/" + config.getName();
        CompilationPlan plan = new CompilationPlanner().build(graph, identity);
        CompilationGroupBuilder builder = new CompilationGroupBuilder();
        ClassLoader previousLoader = nova.getScriptClassLoader();
        ClassLoader hostLoader = previousLoader;
        if (hostLoader == null) {
            hostLoader = Nova.class.getClassLoader();
        }
        CompilationClassLoader loader = new CompilationClassLoader(hostLoader);
        Map<String, Class<?>> allClasses = new LinkedHashMap<>();
        Map<String, Map<String, Class<?>>> classesByGroup = new LinkedHashMap<>();
        Map<String, CompilationExports> exportsByGroup = new LinkedHashMap<>();
        Map<String, String> fingerprints = new LinkedHashMap<>();
        Map<String, Class<?>> moduleClasses = new LinkedHashMap<>();
        Map<String, SourceMap> sourceMaps = new LinkedHashMap<>();
        List<Class<?>> initializers = new ArrayList<>();

        // 各组按依赖顺序加载到本次编译独占的类加载器；缓存不保存 Class 对象。
        try {
            nova.setScriptClassLoader(loader);
            for (CompilationPlan.Group group : plan.getGroups()) {
                CompilationBundle bundle = builder.build(graph, group, exportsByGroup);
                try {
                    // 依赖签名或实现改变时重新编译消费者，避免命中旧的链接字节码。
                    String fingerprint = fingerprint(bundle.getCacheSource(), group, fingerprints);
                    fingerprints.put(group.getId(), fingerprint);
                    BytecodeArtifactCache.CacheKey key = new BytecodeArtifactCache.CacheKey(
                            hostLoader, identity, group.getId(), fingerprint);
                    BytecodeArtifactCache.BytecodeArtifact artifact = cache.getOrCompile(key,
                            () -> nova.compileToBytecodeArtifact(bundle.parse(group.getId())));
                    Map<String, Class<?>> classes = artifact.loadInto(loader);
                    allClasses.putAll(classes);
                    classesByGroup.put(group.getId(), classes);
                    for (String className : classes.keySet()) {
                        sourceMaps.put(className, bundle.getSourceMap());
                    }
                    CompilationExports exports = exportedSymbols(group, classes,
                            builder.collectJavaImportDeclarations(graph, group));
                    bundle.exportExtensions(group.getPackageName(), exports, classes);
                    exportsByGroup.put(group.getId(), exports);
                    // 所有可达模块统一按依赖顺序参与显式 run() 初始化。
                    Class<?> module = classes.get(group.getPackageName() + ".$Module");
                    if (module != null) {
                        initializers.add(module);
                        for (String moduleId : group.getModuleIds()) {
                            moduleClasses.put(moduleId, module);
                        }
                    }
                } catch (RuntimeException failure) {
                    throw bundle.getSourceMap().mapFailure(
                            "Failed to compile module group '" + group.getId() + "' (modules: "
                                    + group.getModuleIds() + ")", failure);
                }
            }

            // 逆拓扑顺序建立唯一函数索引，入口声明优先于依赖声明。
            Map<String, Class<?>> functions = new LinkedHashMap<>();
            List<CompilationPlan.Group> groups = plan.getGroups();
            for (int index = groups.size() - 1; index >= 0; index--) {
                collectFunctions(classesByGroup.get(groups.get(index).getId()), functions);
            }
            CompiledProgramMetadata metadata = new CompiledProgramMetadata(graph, functions, moduleClasses, initializers, sourceMaps);
            return nova.createCompiledNova(allClasses, metadata);
        } finally {
            // 编译器恢复宿主设置；返回程序保留自己的类加载器，不修改 Nova 的后续编译。
            nova.setScriptClassLoader(previousLoader);
        }
    }

    /**
     * 摘要当前源码及全部依赖身份，缓存键随传递依赖变更而失效。
     * @param source 包含行内返回契约的完整编译输入
     * @param group 当前模块编译组
     * @param fingerprints 已完成依赖的源码摘要
     * @return 完整 SHA-256 十六进制摘要
     */
    private String fingerprint(String source, CompilationPlan.Group group, Map<String, String> fingerprints) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(source.getBytes(StandardCharsets.UTF_8));
            for (CompilationPlan.Group dependency : group.getDependencies()) {
                digest.update((byte) 0);
                digest.update(dependency.getId().getBytes(StandardCharsets.UTF_8));
                digest.update((byte) 0);
                digest.update(fingerprints.get(dependency.getId()).getBytes(StandardCharsets.UTF_8));
            }
            StringBuilder result = new StringBuilder();
            for (byte value : digest.digest()) {
                result.append(String.format("%02x", value & 0xff));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new CompilationException("SHA-256 is not available", exception);
        }
    }

    /** 依次建立入口函数索引，入口自身声明优先于依赖声明。 */
    private void collectFunctions(Map<String, Class<?>> classes, Map<String, Class<?>> functions) {
        for (Map.Entry<String, Class<?>> entry : classes.entrySet()) {
            if (!entry.getKey().endsWith("$Module")) {
                continue;
            }
            for (java.lang.reflect.Method method : entry.getValue().getDeclaredMethods()) {
                int modifiers = method.getModifiers();
                if (java.lang.reflect.Modifier.isPublic(modifiers) && java.lang.reflect.Modifier.isStatic(modifiers)) {
                    functions.putIfAbsent(method.getName(), entry.getValue());
                }
            }
        }
    }

    /** 提取类型、单例、静态成员和 Java 导入，供依赖组链接。 */
    private CompilationExports exportedSymbols(
            CompilationPlan.Group group,
            Map<String, Class<?>> classes,
            Set<String> javaImportDeclarations) {
        Set<String> typeNames = new LinkedHashSet<String>();
        Set<String> objectNames = new LinkedHashSet<String>();
        Set<String> staticMemberNames = new LinkedHashSet<String>();
        String prefix = group.getPackageName() + ".";
        for (Map.Entry<String, Class<?>> entry : classes.entrySet()) {
            String className = entry.getKey();
            if (!className.startsWith(prefix)) {
                continue;
            }
            String localName = className.substring(prefix.length());
            if ("$Module".equals(localName)) {
                Class<?> moduleClass = entry.getValue();
                for (java.lang.reflect.Method method : moduleClass.getDeclaredMethods()) {
                    int modifiers = method.getModifiers();
                    if (java.lang.reflect.Modifier.isPublic(modifiers)
                            && java.lang.reflect.Modifier.isStatic(modifiers)
                            && !"main".equals(method.getName())) {
                        staticMemberNames.add(method.getName());
                    }
                }
                for (java.lang.reflect.Field field : moduleClass.getDeclaredFields()) {
                    int modifiers = field.getModifiers();
                    if (java.lang.reflect.Modifier.isPublic(modifiers)
                            && java.lang.reflect.Modifier.isStatic(modifiers)) {
                        staticMemberNames.add(field.getName());
                    }
                }
                continue;
            }
            if (localName.indexOf('$') < 0) {
                Class<?> exportedClass = entry.getValue();
                boolean novaObject = false;
                try {
                    java.lang.reflect.Field instanceField =
                            exportedClass.getDeclaredField("INSTANCE");
                    int modifiers = instanceField.getModifiers();
                    novaObject = java.lang.reflect.Modifier.isPublic(modifiers)
                            && java.lang.reflect.Modifier.isStatic(modifiers)
                            && instanceField.getType() == exportedClass;
                } catch (NoSuchFieldException ignored) {
                    novaObject = false;
                }
                if (novaObject) {
                    objectNames.add(localName);
                } else {
                    typeNames.add(localName);
                }
            }
        }
        return new CompilationExports(
                typeNames, objectNames, staticMemberNames, javaImportDeclarations);
    }

}
