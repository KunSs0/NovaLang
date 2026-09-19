package com.novalang.workspace;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 读取当前 Workspace 根目录内配置文件的通用安全入口。
 */
public final class WorkspaceConfigFiles {

    /**
     * 工具类不允许实例化。
     */
    private WorkspaceConfigFiles() {
    }

    /**
     * 读取 UTF-8 YAML 配置并返回严格类型文档。
     *
     * @param relativePath 相对于 {@code nova.config.yml} 所在目录的路径
     * @return 已解析配置文档
     * @throws WorkspaceException 路径越界、文件不存在或 YAML 非法时抛出
     */
    public static WorkspaceConfigDocument loadYaml(String relativePath) {
        Path file = resolveRequiredFile(relativePath);
        String fileName = file.getFileName().toString().toLowerCase(java.util.Locale.ROOT);
        if (!fileName.endsWith(".yml") && !fileName.endsWith(".yaml")) {
            throw new WorkspaceException("Workspace config file must use .yml or .yaml: " + file);
        }

        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        Yaml yaml = new Yaml(new SafeConstructor(options));
        Object loaded;
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            loaded = yaml.load(reader);
        } catch (IOException | RuntimeException exception) {
            throw new WorkspaceException("Failed to read Workspace YAML config: " + file, exception);
        }
        if (!(loaded instanceof Map)) {
            throw new WorkspaceException("Workspace YAML config root must be a map: " + file);
        }

        Map<?, ?> raw = (Map<?, ?>) loaded;
        Map<String, Object> root = new LinkedHashMap<String, Object>();
        for (Map.Entry<?, ?> entry : raw.entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                throw new WorkspaceException("Workspace YAML config contains a non-string root key: " + file);
            }
            root.put((String) entry.getKey(), entry.getValue());
        }
        return new WorkspaceConfigDocument(file, root);
    }

    /**
     * 判断文档内点分路径是否存在。
     *
     * @param document 已加载的配置文档
     * @param path 点分配置路径
     * @return 节点存在时返回 {@code true}
     */
    public static boolean contains(WorkspaceConfigDocument document, String path) {
        return requireDocument(document).contains(path);
    }

    /**
     * 读取文档内的原始配置值。
     *
     * @param document 已加载的配置文档
     * @param path 点分配置路径
     * @return 原始配置值；节点不存在时返回 {@code null}
     */
    public static Object read(WorkspaceConfigDocument document, String path) {
        return requireDocument(document).get(path);
    }

    /**
     * 读取文档内的字符串。
     *
     * @param document 已加载的配置文档
     * @param path 点分配置路径
     * @return 字符串；节点不存在时返回 {@code null}
     */
    public static String readString(WorkspaceConfigDocument document, String path) {
        return requireDocument(document).getString(path);
    }

    /**
     * 读取文档内的字符串并支持缺失默认值。
     *
     * @param document 已加载的配置文档
     * @param path 点分配置路径
     * @param defaultValue 节点缺失时返回值
     * @return 配置值或默认值
     */
    public static String readString(WorkspaceConfigDocument document,
                                    String path,
                                    String defaultValue) {
        return requireDocument(document).getString(path, defaultValue);
    }

    /**
     * 读取文档内的整数，节点缺失时返回零。
     *
     * @param document 已加载的配置文档
     * @param path 点分配置路径
     * @return 整数值
     */
    public static int readInt(WorkspaceConfigDocument document, String path) {
        return requireDocument(document).getInt(path);
    }

    /**
     * 读取文档内的整数并支持缺失默认值。
     *
     * @param document 已加载的配置文档
     * @param path 点分配置路径
     * @param defaultValue 节点缺失时返回值
     * @return 配置值或默认值
     */
    public static int readInt(WorkspaceConfigDocument document, String path, int defaultValue) {
        return requireDocument(document).getInt(path, defaultValue);
    }

    /**
     * 读取文档内的浮点数，节点缺失时返回零。
     *
     * @param document 已加载的配置文档
     * @param path 点分配置路径
     * @return 浮点值
     */
    public static double readDouble(WorkspaceConfigDocument document, String path) {
        return requireDocument(document).getDouble(path);
    }

    /**
     * 读取文档内的浮点数并支持缺失默认值。
     *
     * @param document 已加载的配置文档
     * @param path 点分配置路径
     * @param defaultValue 节点缺失时返回值
     * @return 配置值或默认值
     */
    public static double readDouble(WorkspaceConfigDocument document, String path, double defaultValue) {
        return requireDocument(document).getDouble(path, defaultValue);
    }

    /**
     * 读取文档内的布尔值，节点缺失时返回 {@code false}。
     *
     * @param document 已加载的配置文档
     * @param path 点分配置路径
     * @return 布尔值
     */
    public static boolean readBoolean(WorkspaceConfigDocument document, String path) {
        return requireDocument(document).getBoolean(path);
    }

    /**
     * 读取文档内的布尔值并支持缺失默认值。
     *
     * @param document 已加载的配置文档
     * @param path 点分配置路径
     * @param defaultValue 节点缺失时返回值
     * @return 配置值或默认值
     */
    public static boolean readBoolean(WorkspaceConfigDocument document,
                                      String path,
                                      boolean defaultValue) {
        return requireDocument(document).getBoolean(path, defaultValue);
    }

    /**
     * 读取文档内的字符串列表，节点缺失时返回空列表。
     *
     * @param document 已加载的配置文档
     * @param path 点分配置路径
     * @return 不可变字符串列表
     */
    public static java.util.List<String> readStringList(WorkspaceConfigDocument document, String path) {
        return requireDocument(document).getStringList(path);
    }

    /**
     * 解析并校验 Workspace 根目录内必须存在的普通文件。
     *
     * @param relativePath 相对路径
     * @return 绝对规范文件路径
     */
    public static Path resolveRequiredFile(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            throw new IllegalArgumentException("relativePath must not be blank");
        }
        Path supplied = java.nio.file.Paths.get(relativePath);
        if (supplied.isAbsolute()) {
            throw new WorkspaceException("Workspace config path must be relative: " + relativePath);
        }
        WorkspaceGeneration generation = WorkspaceExecutionContext.currentGeneration();
        if (generation == null) {
            throw new WorkspaceException("The current thread has no Workspace Generation");
        }
        Path root = generation.getRootDirectory();
        Path resolved = root.resolve(supplied).toAbsolutePath().normalize();
        if (!resolved.startsWith(root)) {
            throw new WorkspaceException("Workspace config path escapes the root directory: " + relativePath);
        }
        if (!Files.isRegularFile(resolved)) {
            throw new WorkspaceException("Required Workspace config file does not exist: " + resolved);
        }
        try {
            Path realRoot = root.toRealPath();
            Path realFile = resolved.toRealPath();
            if (!realFile.startsWith(realRoot)) {
                throw new WorkspaceException("Workspace config path escapes the root directory: " + relativePath);
            }
            return realFile;
        } catch (IOException exception) {
            throw new WorkspaceException("Failed to resolve Workspace config path: " + resolved, exception);
        }
    }

    /**
     * 校验脚本传入的已加载配置文档。
     *
     * @param document 待校验文档
     * @return 非空文档
     */
    private static WorkspaceConfigDocument requireDocument(WorkspaceConfigDocument document) {
        if (document == null) {
            throw new IllegalArgumentException("document must not be null");
        }
        return document;
    }
}
