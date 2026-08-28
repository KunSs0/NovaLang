package com.novalang.mock;

import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import com.novalang.workspace.RuntimeWorkspace;
import com.novalang.workspace.WorkspaceException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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

    public MockTestReport run(Path target,
                              List<Path> sourceRoots,
                              Map<String, Path> aliases,
                              Map<String, Object> initialMocks,
                              MockTestHost host,
                              Consumer<String> output) {
        if (target == null) {
            throw new WorkspaceException("Mock 目标不能为空");
        }
        if (sourceRoots == null || aliases == null || initialMocks == null) {
            throw new WorkspaceException("Mock sourceRoots、aliases 和 initialMocks 不能为空");
        }
        if (host == null) {
            throw new WorkspaceException("Mock host 不能为空");
        }
        List<Path> files = collect(target);
        if (files.isEmpty()) {
            throw new WorkspaceException("没有找到 .mock.nova 文件: " + target);
        }
        NovaScheduler scheduler = SchedulerHolder.get();
        if (scheduler == null) {
            throw new WorkspaceException("NovaLang 调度器尚未安装");
        }
        MockTestReport report = new MockTestReport();
        for (Path file : files) {
            runOne(file, sourceRoots, aliases, initialMocks, host, output, report);
        }
        return report;
    }

    private void runOne(Path file,
                         List<Path> sourceRoots,
                         Map<String, Path> aliases,
                         Map<String, Object> initialMocks,
                         MockTestHost host,
                         Consumer<String> output,
                         MockTestReport report) {
        AtomicInteger assertionCount = new AtomicInteger();
        MockTestBindings bindings = new MockTestBindings(assertionCount, initialMocks);
        RuntimeWorkspace workspace = null;
        Path temporaryRoot = null;
        Throwable failure = null;
        MockTestBindings.Scope scope = bindings.installCurrent();
        try {
            if (!file.getFileName().toString().endsWith(SUFFIX)) {
                throw new WorkspaceException("Mock 文件必须以 " + SUFFIX + " 结尾");
            }
            temporaryRoot = Files.createTempDirectory("novalang-mock-");
            Path config = writeConfig(temporaryRoot, file, sourceRoots, aliases);
            workspace = new RuntimeWorkspace(config, host);
            workspace.load();
            workspace.invoke(moduleEntry(file), "test",
                    Collections.<String, Object>emptyMap(), null, new Object[0]);
        } catch (Exception | AssertionError caught) {
            failure = caught;
        } finally {
            if (workspace != null) {
                try {
                    workspace.dispose();
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
            Throwable temporaryCleanupFailure = deleteTemporaryRoot(temporaryRoot);
            failure = mergeFailure(failure, temporaryCleanupFailure);
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

    private Path writeConfig(Path temporaryRoot,
                             Path file,
                             List<Path> sourceRoots,
                             Map<String, Path> aliases) throws IOException {
        Path testRoot = file.toAbsolutePath().normalize().getParent();
        List<Path> roots = new ArrayList<Path>();
        addPath(roots, testRoot);
        for (Path root : sourceRoots) { addPath(roots, root.toAbsolutePath().normalize()); }
        Path serverRoot = findServerRoot(file);
        addPath(roots, serverRoot);
        Map<String, Path> actualAliases = new LinkedHashMap<String, Path>();
        actualAliases.put("@mock", testRoot);
        actualAliases.put("@", testRoot);
        if (serverRoot != null) {
            addAlias(actualAliases, "@nova", serverRoot.resolve("plugins/NovaLang/libs"));
            addAlias(actualAliases, "@creator", serverRoot.resolve("plugins/Creator/script"));
            addAlias(actualAliases, "@planners", serverRoot.resolve("plugins/Planners/script/planners"));
        }
        for (Map.Entry<String, Path> entry : aliases.entrySet()) {
            String name = entry.getKey();
            Path path = entry.getValue();
            if (name == null || name.isEmpty() || path == null) {
                throw new WorkspaceException("Mock alias 名称和目录不能为空");
            }
            if (actualAliases.containsKey(name)) {
                throw new WorkspaceException("Mock alias 不得覆盖保留映射: " + name);
            }
            actualAliases.put(name, path.toAbsolutePath().normalize());
        }
        for (Path aliasRoot : actualAliases.values()) { addPath(roots, aliasRoot); }
        StringBuilder content = new StringBuilder("version: 1\nname: mock-")
                .append(Integer.toHexString(file.toString().hashCode())).append("\naliases:\n");
        for (Map.Entry<String, Path> entry : actualAliases.entrySet()) {
            content.append("  ").append(yaml(entry.getKey())).append(": ")
                    .append(yaml(entry.getValue().toAbsolutePath().normalize().toString())).append('\n');
        }
        content.append("sources:\n");
        for (Path root : roots) { content.append("  - ").append(yaml(root.toString())).append('\n'); }
        content.append("entries:\n  - ").append(yaml(moduleEntry(file)))
                .append("\nruntime:\n  security: trusted-server\n  thread: caller\n");
        Path config = temporaryRoot.resolve("nova.config.yml");
        Files.write(config, content.toString().getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return config;
    }

    private static String moduleEntry(Path file) { return "@mock/" + file.getFileName(); }
    private static String yaml(String value) { return "'" + value.replace("'", "''") + "'"; }
    private static void addAlias(Map<String, Path> aliases, String name, Path path) {
        if (Files.isDirectory(path)) { aliases.put(name, path.toAbsolutePath().normalize()); }
    }
    private static void addPath(List<Path> paths, Path path) {
        if (path != null && Files.isDirectory(path)) {
            Path normalized = path.toAbsolutePath().normalize();
            if (!paths.contains(normalized)) { paths.add(normalized); }
        }
    }
    private static Path findServerRoot(Path file) {
        Path current = file.toAbsolutePath().normalize().getParent();
        while (current != null) {
            if (Files.isDirectory(current.resolve("plugins"))) { return current; }
            current = current.getParent();
        }
        return null;
    }
    private static List<Path> collect(Path target) {
        if (!Files.exists(target)) { throw new WorkspaceException("Mock 文件或目录不存在: " + target); }
        List<Path> files = new ArrayList<Path>();
        if (Files.isRegularFile(target)) { files.add(target.toAbsolutePath().normalize()); }
        else {
            try (java.util.stream.Stream<Path> stream = Files.walk(target)) {
                stream.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().endsWith(SUFFIX))
                        .forEach(path -> files.add(path.toAbsolutePath().normalize()));
            } catch (IOException exception) {
                throw new WorkspaceException("扫描 mock 目录失败: " + target, exception);
            }
        }
        Collections.sort(files, Comparator.comparing(Path::toString));
        return files;
    }
    private static Throwable deleteTemporaryRoot(Path root) {
        if (root == null || !Files.exists(root)) { return null; }
        try (java.util.stream.Stream<Path> stream = Files.walk(root)) {
            List<Path> paths = new ArrayList<Path>(); stream.forEach(paths::add);
            Collections.sort(paths, Comparator.comparing(Path::toString).reversed());
            for (Path path : paths) { Files.deleteIfExists(path); }
            return null;
        } catch (IOException | RuntimeException failure) {
            return failure;
        }
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
