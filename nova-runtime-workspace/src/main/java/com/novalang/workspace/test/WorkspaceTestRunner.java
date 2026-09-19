package com.novalang.workspace.test;

import com.novalang.runtime.Nova;
import com.novalang.runtime.NovaScheduler;
import com.novalang.runtime.SchedulerHolder;
import com.novalang.runtime.host.JavaTypes;
import com.novalang.test.JavaTypesFactorySpec;
import com.novalang.test.DirectTestScheduler;
import com.novalang.test.TestBindings;
import com.novalang.test.TestJavaTypesLoader;
import com.novalang.test.TestReport;
import com.novalang.workspace.RuntimeWorkspace;
import com.novalang.workspace.SourceUnit;
import com.novalang.workspace.WorkspaceBytecodeArtifactCache;
import com.novalang.workspace.WorkspaceException;
import com.novalang.workspace.WorkspaceHost;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/** 基于现有 Workspace 配置执行 Nova 测试文件。 */
public final class WorkspaceTestRunner {
    private static final String TEST_ENTRY_PREFIX = "@test/";

    public TestReport run(Path target, Consumer<String> output) {
        if (target == null) {
            throw new WorkspaceException("Test target must not be null");
        }
        Path configFile = findConfig(target);
        WorkspaceTestConfig config = new WorkspaceTestConfigLoader().load(configFile);
        List<WorkspaceTestConfig.TestCase> cases = selectCases(target, config);
        if (cases.isEmpty()) {
            throw new WorkspaceException("No Nova test files found: " + target);
        }

        NovaScheduler previous = SchedulerHolder.get();
        DirectTestScheduler direct = null;
        if (previous == null) {
            direct = new DirectTestScheduler();
            SchedulerHolder.set(direct);
        }
        TestReport report = new TestReport();
        try {
            for (WorkspaceTestConfig.TestCase testCase : cases) {
                runOne(config, testCase, output, report);
            }
            return report;
        } finally {
            if (direct != null) {
                SchedulerHolder.clear();
                direct.close();
            }
        }
    }

    private void runOne(WorkspaceTestConfig config,
                        WorkspaceTestConfig.TestCase testCase,
                        Consumer<String> output,
                        TestReport report) {
        AtomicInteger assertions = new AtomicInteger();
        TestBindings bindings = new TestBindings(assertions, Collections.<String, Object>emptyMap());
        List<String> dependencyIds = mergeClasspath(config.getClasspath(), testCase.getClasspath());
        List<WorkspaceTestConfig.Dependency> dependencies = resolveDependencies(
                dependencyIds, config.getDependencies());
        URLClassLoader classLoader = createClassLoader(config.getRuntime(), dependencies);
        RuntimeWorkspace workspace = null;
        WorkspaceHost workspaceHost = null;
        Throwable failure = null;
        try {
            List<JavaTypesFactorySpec> factories = collectFactories(dependencies);
            List<JavaTypes> javaTypes = loadJavaTypes(factories, classLoader);
            workspaceHost = new WorkspaceTestHost(bindings, javaTypes);
            workspace = new RuntimeWorkspace(config.getConfigFile(), workspaceHost,
                    new WorkspaceBytecodeArtifactCache(), classLoader, false);
            String source = readUtf8(testCase.getFile());
            String entry = TEST_ENTRY_PREFIX + testCase.getFile().getFileName().toString();
            workspace.registerVirtualSource(SourceUnit.physical(entry, source, testCase.getFile()),
                    true, false);
            workspace.load();
            workspace.invoke(entry, "test", Collections.<String, Object>emptyMap(), null,
                    new Object[0]);
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
            if (workspaceHost instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) workspaceHost).close();
                } catch (Exception cleanupFailure) {
                    failure = mergeFailure(failure, cleanupFailure);
                }
            }
            try {
                classLoader.close();
            } catch (IOException cleanupFailure) {
                failure = mergeFailure(failure, cleanupFailure);
            }
        }

        if (failure == null) {
            report.add(new TestReport.TestResult(testCase.getFile(), true,
                    assertions.get(), null));
            send(output, "[PASS] " + testCase.getFile()
                    + " (assertions=" + assertions.get() + ")");
        } else {
            String message = describeFailure(failure);
            report.add(new TestReport.TestResult(testCase.getFile(), false,
                    assertions.get(), message));
            send(output, "[FAIL] " + testCase.getFile() + "\n       " + message);
        }
    }

    private List<JavaTypes> loadJavaTypes(List<JavaTypesFactorySpec> factories,
                                          ClassLoader classLoader) {
        return new TestJavaTypesLoader().load(factories, classLoader);
    }

    private List<JavaTypesFactorySpec> collectFactories(
            List<WorkspaceTestConfig.Dependency> dependencies) {
        List<JavaTypesFactorySpec> result = new ArrayList<JavaTypesFactorySpec>();
        Set<String> seen = new LinkedHashSet<String>();
        for (WorkspaceTestConfig.Dependency dependency : dependencies) {
            for (JavaTypesFactorySpec spec : dependency.getJavaTypes()) {
                String key = spec.getClassName() + "#" + spec.getMethodName();
                if (seen.add(key)) {
                    result.add(spec);
                }
            }
        }
        return result;
    }

    private URLClassLoader createClassLoader(List<Path> runtime,
                                             List<WorkspaceTestConfig.Dependency> dependencies) {
        try {
            List<URL> urls = new ArrayList<URL>();
            Set<Path> seen = new LinkedHashSet<Path>();
            for (Path path : runtime) {
                addRuntimePath(path, urls, seen);
            }
            for (WorkspaceTestConfig.Dependency dependency : dependencies) {
                if (seen.add(dependency.getJar())) {
                    urls.add(dependency.getJar().toUri().toURL());
                }
            }
            return new TestClassLoader(urls.toArray(new URL[urls.size()]),
                    WorkspaceTestRunner.class.getClassLoader());
        } catch (IOException exception) {
            throw new WorkspaceException("Failed to create test class loader", exception);
        }
    }

    private void addRuntimePath(Path path, List<URL> urls, Set<Path> seen) throws IOException {
        if (Files.isRegularFile(path)) {
            if (seen.add(path)) {
                urls.add(path.toUri().toURL());
            }
            return;
        }
        if (!Files.isDirectory(path)) {
            throw new WorkspaceException("Test runtime path must be a JAR or directory: " + path);
        }
        try (java.util.stream.Stream<Path> stream = Files.walk(path)) {
            stream.filter(Files::isRegularFile)
                    .filter(item -> item.getFileName().toString().endsWith(".jar"))
                    .sorted()
                    .forEach(item -> {
                        Path normalized = item.toAbsolutePath().normalize();
                        if (seen.add(normalized)) {
                            try {
                                urls.add(normalized.toUri().toURL());
                            } catch (IOException exception) {
                                throw new RuntimeException(exception);
                            }
                        }
                    });
        } catch (RuntimeException exception) {
            if (exception.getCause() instanceof IOException) {
                throw (IOException) exception.getCause();
            }
            throw exception;
        }
    }

    private List<WorkspaceTestConfig.Dependency> resolveDependencies(
            List<String> ids, Map<String, WorkspaceTestConfig.Dependency> dependencies) {
        List<WorkspaceTestConfig.Dependency> result = new ArrayList<WorkspaceTestConfig.Dependency>();
        Set<String> seen = new LinkedHashSet<String>();
        for (String id : ids) {
            WorkspaceTestConfig.Dependency dependency = dependencies.get(id);
            if (dependency == null) {
                throw new WorkspaceException("Unknown test dependency: " + id);
            }
            if (seen.add(id)) {
                result.add(dependency);
            }
        }
        return result;
    }

    private List<String> mergeClasspath(List<String> common, List<String> extra) {
        LinkedHashSet<String> merged = new LinkedHashSet<String>();
        merged.addAll(common);
        merged.addAll(extra);
        return new ArrayList<String>(merged);
    }

    private List<WorkspaceTestConfig.TestCase> selectCases(Path target,
                                                            WorkspaceTestConfig config) {
        Path normalized = target.toAbsolutePath().normalize();
        if (!Files.exists(normalized)) {
            throw new WorkspaceException("Nova test file or directory does not exist: " + normalized);
        }
        List<WorkspaceTestConfig.TestCase> declared = config.getCases();
        if (Files.isRegularFile(normalized)) {
            if (!normalized.getFileName().toString().endsWith(".mock.nova")) {
                throw new WorkspaceException("Nova test file must end with .mock.nova: " + normalized);
            }
            for (WorkspaceTestConfig.TestCase testCase : declared) {
                if (testCase.getFile().equals(normalized)) {
                    return Collections.singletonList(testCase);
                }
            }
            return Collections.singletonList(new WorkspaceTestConfig.TestCase(normalized,
                    Collections.<String>emptyList()));
        }
        List<WorkspaceTestConfig.TestCase> result = new ArrayList<WorkspaceTestConfig.TestCase>();
        if (!declared.isEmpty()) {
            for (WorkspaceTestConfig.TestCase testCase : declared) {
                if (testCase.getFile().startsWith(normalized)) {
                    result.add(testCase);
                }
            }
        } else {
            List<String> roots = config.getRoots();
            if (roots.isEmpty()) {
                roots = Collections.singletonList(".");
            }
            List<String> includes = config.getIncludes();
            if (includes.isEmpty()) {
                includes = Collections.singletonList("*.mock.nova");
            }
            for (String root : roots) {
                Path directory = config.getRootDirectory().resolve(root).normalize();
                collectFiles(directory, includes, result);
            }
        }
        Map<Path, WorkspaceTestConfig.TestCase> unique =
                new LinkedHashMap<Path, WorkspaceTestConfig.TestCase>();
        for (WorkspaceTestConfig.TestCase testCase : result) {
            unique.put(testCase.getFile(), testCase);
        }
        result = new ArrayList<WorkspaceTestConfig.TestCase>(unique.values());
        Collections.sort(result, Comparator.comparing(item -> item.getFile().toString()));
        return result;
    }

    private void collectFiles(Path directory, List<String> includes,
                               List<WorkspaceTestConfig.TestCase> result) {
        if (!Files.isDirectory(directory)) {
            return;
        }
        try {
            try (java.util.stream.Stream<Path> stream = Files.walk(directory)) {
                stream.filter(Files::isRegularFile).forEach(path -> {
                    String name = path.getFileName().toString();
                    boolean matched = false;
                    for (String include : includes) {
                        if (include.startsWith("*")) {
                            matched = name.endsWith(include.substring(1));
                        } else {
                            matched = name.equals(include);
                        }
                        if (matched) {
                            break;
                        }
                    }
                    if (matched) {
                        result.add(new WorkspaceTestConfig.TestCase(
                                path.toAbsolutePath().normalize(), Collections.<String>emptyList()));
                    }
                });
            }
        } catch (IOException exception) {
            throw new WorkspaceException("Failed to scan test directory: " + directory, exception);
        }
    }

    private Path findConfig(Path target) {
        Path current = target.toAbsolutePath().normalize();
        if (!Files.isDirectory(current)) {
            current = current.getParent();
        }
        while (current != null) {
            Path candidate = current.resolve("nova.config.yml");
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
            current = current.getParent();
        }
        throw new WorkspaceException("Cannot find nova.config.yml for test target: " + target);
    }

    private static String readUtf8(Path file) {
        try {
            return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new WorkspaceException("Failed to read test file: " + file, exception);
        }
    }

    private static void send(Consumer<String> output, String message) {
        if (output != null) {
            output.accept(message);
        }
    }

    private static Throwable mergeFailure(Throwable current, Throwable additional) {
        if (current == null) {
            return additional;
        }
        current.addSuppressed(additional);
        return current;
    }

    private static String describeFailure(Throwable failure) {
        StringBuilder text = new StringBuilder();
        Throwable current = failure;
        while (current != null) {
            if (text.length() > 0) {
                text.append(" -> ");
            }
            text.append(current.getClass().getSimpleName());
            if (current.getMessage() != null) {
                text.append(": ").append(current.getMessage());
            }
            current = current.getCause();
        }
        return text.toString();
    }

    private static final class WorkspaceTestHost implements WorkspaceHost, AutoCloseable {
        private final TestBindings bindings;
        private final List<JavaTypes> javaTypes;

        private WorkspaceTestHost(TestBindings bindings, List<JavaTypes> javaTypes) {
            this.bindings = bindings;
            this.javaTypes = javaTypes;
        }

        @Override
        public void install(Nova nova) {
            for (JavaTypes types : javaTypes) {
                nova.install(types);
            }
            bindings.install(nova);
        }

        @Override
        public void close() {
        }
    }

    /**
     * 测试依赖使用子优先加载，避免单 JAR 模式下 NovaBukkit 被 CLI 主类加载器抢先加载。
     * Nova 运行时自身的类型保持父加载器身份，保证 JavaTypes 等接口不会出现类型分裂。
     */
    private static final class TestClassLoader extends URLClassLoader {
        private TestClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                Class<?> loaded = findLoadedClass(name);
                if (loaded == null && !isParentFirst(name)) {
                    try {
                        loaded = findClass(name);
                    } catch (ClassNotFoundException ignored) {
                        loaded = null;
                    }
                }
                if (loaded == null) {
                    loaded = super.loadClass(name, false);
                }
                if (resolve) {
                    resolveClass(loaded);
                }
                return loaded;
            }
        }

        private boolean isParentFirst(String name) {
            return name.startsWith("java.")
                    || name.startsWith("javax.")
                    || name.startsWith("jdk.")
                    || name.startsWith("sun.")
                    || name.startsWith("com.novalang.runtime.")
                    || name.startsWith("com.novalang.workspace.")
                    || name.startsWith("com.novalang.test.")
                    || name.startsWith("com.novalang.compiler.")
                    || name.startsWith("com.novalang.ir.")
                    || name.startsWith("com.novalang.script.");
        }
    }
}
