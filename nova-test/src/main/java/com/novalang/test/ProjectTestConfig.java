package com.novalang.test;

import com.novalang.test.JavaTypesFactorySpec;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 从 nova.config.yml 提取的 Program 测试配置。 */
public final class ProjectTestConfig {
    private final Path configFile;
    private final Map<String, Dependency> dependencies;
    private final List<Path> runtime;
    private final List<String> classpath;
    private final List<TestCase> cases;
    private final List<String> roots;
    private final List<String> includes;

    ProjectTestConfig(Path configFile,
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

    /** @return 编译和测试配置文件的绝对路径 */
    public Path getConfigFile() {
        return configFile;
    }

    /** @return 配置文件所在的项目根目录 */
    public Path getRootDirectory() {
        return configFile.getParent();
    }

    /** @return 按 ID 索引的测试依赖 */
    public Map<String, Dependency> getDependencies() {
        return dependencies;
    }

    /** @return 当前测试启用的依赖 ID */
    public List<String> getClasspath() {
        return classpath;
    }

    /** @return 显式声明的运行时 JAR 或目录 */
    public List<Path> getRuntime() {
        return runtime;
    }

    /** @return 配置中明确列出的测试用例 */
    public List<TestCase> getCases() {
        return cases;
    }

    /** @return 递归查找测试文件的根目录 */
    public List<String> getRoots() {
        return roots;
    }

    /** @return 测试文件包含规则 */
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

        /** @return 测试依赖 ID */
        public String getId() {
            return id;
        }

        /** @return 依赖 JAR 的绝对路径 */
        public Path getJar() {
            return jar;
        }

        /** @return 依赖提供的 JavaTypes 工厂 */
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

        /** @return 测试文件绝对路径 */
        public Path getFile() {
            return file;
        }

        /** @return 当前测试启用的依赖 ID */
        public List<String> getClasspath() {
            return classpath;
        }
    }
}
