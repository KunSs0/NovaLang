package com.novalang.workspace.test;

import com.novalang.test.JavaTypesFactorySpec;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 从 nova.config.yml 提取的 Workspace 测试配置。 */
public final class WorkspaceTestConfig {
    private final Path configFile;
    private final Map<String, Dependency> dependencies;
    private final List<Path> runtime;
    private final List<String> classpath;
    private final List<TestCase> cases;
    private final List<String> roots;
    private final List<String> includes;

    WorkspaceTestConfig(Path configFile,
                         Map<String, Dependency> dependencies,
                         List<Path> runtime,
                         List<String> classpath,
                         List<TestCase> cases,
                         List<String> roots,
                         List<String> includes) {
        this.configFile = configFile;
        this.dependencies = Collections.unmodifiableMap(new LinkedHashMap<String, Dependency>(dependencies));
        this.runtime = Collections.unmodifiableList(new ArrayList<Path>(runtime));
        this.classpath = Collections.unmodifiableList(new ArrayList<String>(classpath));
        this.cases = Collections.unmodifiableList(new ArrayList<TestCase>(cases));
        this.roots = Collections.unmodifiableList(new ArrayList<String>(roots));
        this.includes = Collections.unmodifiableList(new ArrayList<String>(includes));
    }

    public Path getConfigFile() {
        return configFile;
    }

    public Path getRootDirectory() {
        return configFile.getParent();
    }

    public Map<String, Dependency> getDependencies() {
        return dependencies;
    }

    public List<String> getClasspath() {
        return classpath;
    }

    public List<Path> getRuntime() {
        return runtime;
    }

    public List<TestCase> getCases() {
        return cases;
    }

    public List<String> getRoots() {
        return roots;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public static final class Dependency {
        private final String id;
        private final Path jar;
        private final List<JavaTypesFactorySpec> javaTypes;

        Dependency(String id, Path jar, List<JavaTypesFactorySpec> javaTypes) {
            this.id = id;
            this.jar = jar;
            this.javaTypes = Collections.unmodifiableList(new ArrayList<JavaTypesFactorySpec>(javaTypes));
        }

        public String getId() {
            return id;
        }

        public Path getJar() {
            return jar;
        }

        public List<JavaTypesFactorySpec> getJavaTypes() {
            return javaTypes;
        }
    }

    public static final class TestCase {
        private final Path file;
        private final List<String> classpath;

        TestCase(Path file, List<String> classpath) {
            this.file = file;
            this.classpath = Collections.unmodifiableList(new ArrayList<String>(classpath));
        }

        public Path getFile() {
            return file;
        }

        public List<String> getClasspath() {
            return classpath;
        }
    }
}
