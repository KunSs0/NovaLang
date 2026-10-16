package com.novalang.mock;

import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import com.novalang.runtime.Nova;
import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.compilation.CompilationConfig;
import com.novalang.runtime.compilation.CompilationConfigLoader;
import com.novalang.runtime.compilation.CompilationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 与 Bukkit 无关的 .mock.nova runner。平台通过 MockTestHost 提供真实类加载器和绑定。
 */
public final class MockTestRunner {
    private static final String SUFFIX = ".mock.nova";

    /**
     * 发现测试文件，每个文件分别作为唯一编译入口执行并汇总结果。
     * @param target 测试文件或目录
     * @param aliases 宿主显式提供的编译路径别名
     * @param initialMocks 每份测试的初始 Mock 绑定
     * @param host 安装平台类型和绑定的宿主
     * @param output 可为空的结果输出回调
     * @return 全部测试的执行报告
     */
    public MockTestReport run(Path target,
                              Map<String, Path> aliases,
                              Map<String, Object> initialMocks,
                              MockTestHost host,
                              Consumer<String> output) {
        if (target == null) {
            throw new CompilationException("Mock 目标不能为空");
        }
        if (aliases == null || initialMocks == null) {
            throw new CompilationException("Mock aliases 和 initialMocks 不能为空");
        }
        if (host == null) {
            throw new CompilationException("Mock host 不能为空");
        }
        List<Path> files = collect(target);
        if (files.isEmpty()) {
            throw new CompilationException("没有找到 .mock.nova 文件: " + target);
        }
        NovaScheduler scheduler = SchedulerHolder.get();
        if (scheduler == null) {
            throw new CompilationException("NovaLang 调度器尚未安装");
        }
        MockTestReport report = new MockTestReport();
        for (Path file : files) {
            runOne(file, aliases, initialMocks, host, output, report);
        }
        return report;
    }

    /**
     * 为单个测试编译独立程序，执行 test() 并释放脚本与宿主资源。
     * @param file 唯一测试入口
     * @param aliases 额外路径别名
     * @param initialMocks 初始 Mock 绑定
     * @param host 平台宿主
     * @param output 可为空的输出回调
     * @param report 汇总报告
     */
    private void runOne(Path file,
                         Map<String, Path> aliases,
                         Map<String, Object> initialMocks,
                         MockTestHost host,
                         Consumer<String> output,
                         MockTestReport report) {
        AtomicInteger assertionCount = new AtomicInteger();
        MockTestBindings bindings = new MockTestBindings(assertionCount, initialMocks);
        CompiledNova program = null;
        Throwable failure = null;
        MockTestBindings.Scope scope = bindings.installCurrent();
        try {
            if (!file.getFileName().toString().endsWith(SUFFIX)) {
                throw new CompilationException("Mock 文件必须以 " + SUFFIX + " 结尾");
            }
            CompilationConfig config = createConfig(file, aliases);
            Nova nova = new Nova();
            nova.setScriptClassLoader(host.getClass().getClassLoader());
            host.install(nova);
            program = nova.compileToBytecode(file, config);
            program.run();
            program.call("test");
        } catch (Exception | AssertionError caught) {
            failure = caught;
        } finally {
            if (program != null) {
                try {
                    program.close();
                } catch (RuntimeException cleanupFailure) {
                    failure = mergeFailure(failure, cleanupFailure);
                }
            }
            try {
                host.close();
            } catch (Exception cleanupFailure) {
                failure = mergeFailure(failure, cleanupFailure);
            }
            try {
                scope.close();
            } catch (RuntimeException cleanupFailure) {
                failure = mergeFailure(failure, cleanupFailure);
            }
        }
        if (failure == null) {
            report.add(new MockTestReport.TestCase(file, true, assertionCount.get(), null));
            send(output, "[PASS] " + file + " (assertions=" + assertionCount.get() + ")");
        } else {
            String message = describeFailure(failure);
            report.add(new MockTestReport.TestCase(file, false, assertionCount.get(), message));
            send(output, "[FAIL] " + file + "\n       " + message);
        }
    }

    /**
     * 收集测试和平台路径别名，入口文件由编译调用单独传入。
     * @param file 用于定位测试目录和服务端环境的入口文件
     * @param aliases 宿主显式提供的别名
     * @return 不包含入口列表或源码目录的编译元数据
     */
    private CompilationConfig createConfig(Path file, Map<String, Path> aliases) {
        Path testRoot = file.toAbsolutePath().normalize().getParent();
        Path serverRoot = findServerRoot(file);
        Map<String, Path> actualAliases = new LinkedHashMap<String, Path>();
        actualAliases.put("@mock", testRoot);
        actualAliases.put("@", testRoot);
        if (serverRoot != null) {
            Path novaLibraries = serverRoot.resolve("plugins").resolve("NovaLang").resolve("libs");
            if (!Files.isDirectory(novaLibraries)) {
                throw new CompilationException("服务端缺少 NovaLang 全局前置库: " + novaLibraries);
            }
            actualAliases.put("@nova", novaLibraries.toAbsolutePath().normalize());
            discoverCompilationConfig(serverRoot, actualAliases);
        }
        for (Map.Entry<String, Path> entry : aliases.entrySet()) {
            String name = entry.getKey();
            Path path = entry.getValue();
            if (name == null || name.isEmpty() || path == null) {
                throw new CompilationException("Mock alias 名称和目录不能为空");
            }
            if (actualAliases.containsKey(name)) {
                throw new CompilationException("Mock alias 不得覆盖保留映射: " + name);
            }
            actualAliases.put(name, path.toAbsolutePath().normalize());
        }
        CompilationConfig.Builder builder = new CompilationConfig.Builder();
        builder.name("mock-" + Integer.toHexString(file.toString().hashCode()));
        for (Map.Entry<String, Path> entry : actualAliases.entrySet()) {
            builder.alias(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    /**
     * 读取服务端项目的别名元数据，发现冲突时明确报错。
     * @param serverRoot 服务端根目录
     * @param aliases 接收项目别名的映射
     */
    private static void discoverCompilationConfig(Path serverRoot,
                                                Map<String, Path> aliases) {
        Path pluginsRoot = serverRoot.resolve("plugins");
        List<Path> configFiles = new ArrayList<Path>();
        try (java.util.stream.Stream<Path> stream = Files.walk(pluginsRoot)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> "nova.config.yml".equals(path.getFileName().toString()))
                    .forEach(configFiles::add);
        } catch (IOException exception) {
            throw new CompilationException("扫描服务端编译配置失败: " + pluginsRoot, exception);
        }
        Collections.sort(configFiles, Comparator.comparing(Path::toString));
        CompilationConfigLoader loader = new CompilationConfigLoader();
        for (Path configFile : configFiles) {
            CompilationConfig config = loader.load(configFile);
            for (Map.Entry<String, Path> entry : config.getAliases().entrySet()) {
                String alias = entry.getKey();
                if ("@nova".equals(alias)) {
                    continue;
                }
                Path path = entry.getValue().toAbsolutePath().normalize();
                Path existing = aliases.get(alias);
                if (existing != null && !existing.equals(path)) {
                    throw new CompilationException("服务端编译配置 Alias 冲突: " + alias
                            + " -> " + existing + " / " + path);
                }
                aliases.put(alias, path);
            }
        }
    }
    private static Path findServerRoot(Path file) {
        Path current = file.toAbsolutePath().normalize().getParent();
        while (current != null) {
            boolean hasPlugins = Files.isDirectory(current.resolve("plugins"));
            boolean hasNovaLibraries = Files.isDirectory(current.resolve("plugins")
                    .resolve("NovaLang").resolve("libs"));
            if (hasPlugins && hasNovaLibraries) { return current; }
            current = current.getParent();
        }
        return null;
    }
    private static List<Path> collect(Path target) {
        if (!Files.exists(target)) { throw new CompilationException("Mock 文件或目录不存在: " + target); }
        List<Path> files = new ArrayList<Path>();
        if (Files.isRegularFile(target)) { files.add(target.toAbsolutePath().normalize()); }
        else {
            try (java.util.stream.Stream<Path> stream = Files.walk(target)) {
                stream.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().endsWith(SUFFIX))
                        .forEach(path -> files.add(path.toAbsolutePath().normalize()));
            } catch (IOException exception) {
                throw new CompilationException("扫描 mock 目录失败: " + target, exception);
            }
        }
        Collections.sort(files, Comparator.comparing(Path::toString));
        return files;
    }
    private static void send(Consumer<String> output, String message) { if (output != null) { output.accept(message); } }
    private static String describeFailure(Throwable failure) {
        StringBuilder text = new StringBuilder(); Throwable current = failure;
        while (current != null) {
            if (text.length() > 0) { text.append(" -> "); }
            text.append(current.getClass().getSimpleName());
            if (current.getMessage() != null) { text.append(": ").append(current.getMessage()); }
            current = current.getCause();
        }
        return text.toString();
    }

    private static Throwable mergeFailure(Throwable current, Throwable additional) {
        if (additional == null) {
            return current;
        }
        if (current == null) {
            return additional;
        }
        current.addSuppressed(additional);
        return current;
    }
}
