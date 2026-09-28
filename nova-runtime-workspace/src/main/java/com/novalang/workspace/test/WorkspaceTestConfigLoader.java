package com.novalang.workspace.test;

import com.novalang.test.JavaTypesFactorySpec;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 读取 nova.config.yml 中 test 区块的严格加载器。 */
public final class WorkspaceTestConfigLoader {
    public WorkspaceTestConfig load(Path configFile) {
        if (configFile == null) {
            throw new IllegalArgumentException("configFile must not be null");
        }
        Path absolute = configFile.toAbsolutePath().normalize();
        if (!Files.isRegularFile(absolute)) {
            throw new IllegalArgumentException("Workspace config file does not exist: " + absolute);
        }
        Object loaded;
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        try (InputStream input = Files.newInputStream(absolute)) {
            loaded = new Yaml(new SafeConstructor(options)).load(input);
        } catch (IOException | RuntimeException exception) {
            throw new IllegalArgumentException("Failed to read Workspace test config: " + absolute, exception);
        }
        Map<String, Object> root = map(loaded, "config root");
        Object rawTest = root.get("test");
        if (rawTest == null) {
            return new WorkspaceTestConfig(absolute, new LinkedHashMap<String, WorkspaceTestConfig.Dependency>(),
                    new ArrayList<Path>(),
                    new ArrayList<String>(), new ArrayList<WorkspaceTestConfig.TestCase>(),
                    new ArrayList<String>(), new ArrayList<String>());
        }
        Map<String, Object> test = map(rawTest, "test");
        Map<String, WorkspaceTestConfig.Dependency> dependencies = parseDependencies(
                test.get("dependencies"), absolute.getParent());
        List<Path> runtime = parseRuntime(test.get("runtime"), absolute.getParent());
        List<String> classpath = strings(test.get("classpath"), "test.classpath");
        List<WorkspaceTestConfig.TestCase> cases = parseCases(test.get("cases"), absolute.getParent());
        List<String> roots = strings(test.get("roots"), "test.roots");
        List<String> includes = strings(test.get("include"), "test.include");
        return new WorkspaceTestConfig(absolute, dependencies, runtime, classpath, cases, roots, includes);
    }

    private List<Path> parseRuntime(Object value, Path root) {
        List<Path> result = new ArrayList<Path>();
        if (value == null) {
            return result;
        }
        List<String> paths = strings(value, "test.runtime");
        for (String path : paths) {
            Path resolved = root.resolve(path).toAbsolutePath().normalize();
            if (!Files.exists(resolved)) {
                throw new IllegalArgumentException("Test runtime path does not exist: " + resolved);
            }
            result.add(resolved);
        }
        return result;
    }

    private Map<String, WorkspaceTestConfig.Dependency> parseDependencies(Object value, Path root) {
        if (value == null) {
            return new LinkedHashMap<String, WorkspaceTestConfig.Dependency>();
        }
        Map<String, Object> raw = map(value, "test.dependencies");
        Map<String, WorkspaceTestConfig.Dependency> result = new LinkedHashMap<String, WorkspaceTestConfig.Dependency>();
        for (Map.Entry<String, Object> entry : raw.entrySet()) {
            Map<String, Object> dependency = map(entry.getValue(), "test.dependencies." + entry.getKey());
            String jar = string(dependency.get("jar"), "test.dependencies." + entry.getKey() + ".jar");
            Path jarPath = root.resolve(jar).toAbsolutePath().normalize();
            if (!Files.isRegularFile(jarPath)) {
                throw new IllegalArgumentException("Test dependency JAR does not exist: " + jarPath);
            }
            List<JavaTypesFactorySpec> factories = parseJavaTypes(
                    dependency.get("java-types"), "test.dependencies." + entry.getKey() + ".java-types");
            result.put(entry.getKey(), new WorkspaceTestConfig.Dependency(entry.getKey(), jarPath, factories));
        }
        return result;
    }

    private List<JavaTypesFactorySpec> parseJavaTypes(Object value, String field) {
        if (value == null) {
            return new ArrayList<JavaTypesFactorySpec>();
        }
        List<?> raw = list(value, field);
        List<JavaTypesFactorySpec> result = new ArrayList<JavaTypesFactorySpec>();
        for (int index = 0; index < raw.size(); index++) {
            Object item = raw.get(index);
            if (item instanceof String) {
                String text = (String) item;
                int separator = text.indexOf('#');
                if (separator < 0) {
                    result.add(new JavaTypesFactorySpec(text, "create"));
                    continue;
                }
                if (separator == 0 || separator == text.length() - 1) {
                    throw new IllegalArgumentException(field + "[" + index + "] must use Class#method");
                }
                result.add(new JavaTypesFactorySpec(text.substring(0, separator),
                        text.substring(separator + 1)));
            } else {
                Map<String, Object> map = map(item, field + "[" + index + "]");
                Object method = map.get("method");
                result.add(new JavaTypesFactorySpec(
                        string(map.get("class"), field + "[" + index + "].class"),
                        method == null ? "create" : string(method, field + "[" + index + "].method")));
            }
        }
        return result;
    }

    private List<WorkspaceTestConfig.TestCase> parseCases(Object value, Path root) {
        if (value == null) {
            return new ArrayList<WorkspaceTestConfig.TestCase>();
        }
        List<?> raw = list(value, "test.cases");
        List<WorkspaceTestConfig.TestCase> result = new ArrayList<WorkspaceTestConfig.TestCase>();
        Set<Path> seen = new LinkedHashSet<Path>();
        for (int index = 0; index < raw.size(); index++) {
            Map<String, Object> item = map(raw.get(index), "test.cases[" + index + "]");
            String file = string(item.get("file"), "test.cases[" + index + "].file");
            Path path = root.resolve(file).toAbsolutePath().normalize();
            if (!Files.isRegularFile(path)) {
                throw new IllegalArgumentException("Test file does not exist: " + path);
            }
            if (!seen.add(path)) {
                throw new IllegalArgumentException("Duplicate test case: " + path);
            }
            result.add(new WorkspaceTestConfig.TestCase(path,
                    strings(item.get("classpath"), "test.cases[" + index + "].classpath")));
        }
        return result;
    }

    private static List<String> strings(Object value, String field) {
        if (value == null) {
            return new ArrayList<String>();
        }
        List<?> raw = list(value, field);
        List<String> result = new ArrayList<String>();
        for (int index = 0; index < raw.size(); index++) {
            result.add(string(raw.get(index), field + "[" + index + "]"));
        }
        return result;
    }

    private static String string(Object value, String field) {
        if (!(value instanceof String) || ((String) value).trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must be a non-empty string");
        }
        return ((String) value).trim();
    }

    private static List<?> list(Object value, String field) {
        if (!(value instanceof List)) {
            throw new IllegalArgumentException(field + " must be a list");
        }
        return (List<?>) value;
    }

    private static Map<String, Object> map(Object value, String field) {
        if (!(value instanceof Map)) {
            throw new IllegalArgumentException(field + " must be a map");
        }
        Map<?, ?> raw = (Map<?, ?>) value;
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        for (Map.Entry<?, ?> entry : raw.entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                throw new IllegalArgumentException(field + " contains a non-string key");
            }
            result.put((String) entry.getKey(), entry.getValue());
        }
        return result;
    }
}
