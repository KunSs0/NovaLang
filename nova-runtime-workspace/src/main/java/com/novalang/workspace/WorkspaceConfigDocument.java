package com.novalang.workspace;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 通过点分路径读取的严格 Workspace YAML 配置文档。
 */
public final class WorkspaceConfigDocument {

    private final Path sourceFile;
    private final Map<String, Object> root;

    /**
     * 创建已解析配置文档。
     *
     * @param sourceFile 配置来源文件
     * @param root YAML 根映射
     */
    WorkspaceConfigDocument(Path sourceFile, Map<String, Object> root) {
        this.sourceFile = sourceFile;
        this.root = Collections.unmodifiableMap(root);
    }

    /** @return 配置来源绝对路径 */
    public Path getSourceFile() {
        return sourceFile;
    }

    /**
     * 判断点分路径是否存在。
     *
     * @param path 配置路径
     * @return 节点存在时返回 {@code true}
     */
    public boolean contains(String path) {
        return find(path) != null;
    }

    /**
     * 读取原始配置值。
     *
     * @param path 点分路径
     * @return 原始值；节点不存在时返回 {@code null}
     */
    public Object get(String path) {
        return find(path);
    }

    /**
     * 读取字符串。
     *
     * @param path 点分路径
     * @return 字符串；节点不存在时返回 {@code null}
     */
    public String getString(String path) {
        return getString(path, null);
    }

    /**
     * 读取字符串并支持缺失默认值。
     *
     * @param path 点分路径
     * @param defaultValue 节点缺失时返回值
     * @return 配置值或默认值
     */
    public String getString(String path, String defaultValue) {
        Object value = find(path);
        if (value == null) {
            return defaultValue;
        }
        if (!(value instanceof String)) {
            throw typeError(path, "a string", value);
        }
        return (String) value;
    }

    /**
     * 读取整数，节点缺失时返回零。
     *
     * @param path 点分路径
     * @return 整数值
     */
    public int getInt(String path) {
        return getInt(path, 0);
    }

    /**
     * 读取整数并支持缺失默认值。
     *
     * @param path 点分路径
     * @param defaultValue 节点缺失时返回值
     * @return 配置值或默认值
     */
    public int getInt(String path, int defaultValue) {
        Object value = find(path);
        if (value == null) {
            return defaultValue;
        }
        if (!(value instanceof Number)) {
            throw typeError(path, "an integer", value);
        }
        Number number = (Number) value;
        int result = number.intValue();
        if (number.doubleValue() != result) {
            throw typeError(path, "an integer", value);
        }
        return result;
    }

    /**
     * 读取浮点数，节点缺失时返回零。
     *
     * @param path 点分路径
     * @return 浮点值
     */
    public double getDouble(String path) {
        return getDouble(path, 0.0d);
    }

    /**
     * 读取浮点数并支持缺失默认值。
     *
     * @param path 点分路径
     * @param defaultValue 节点缺失时返回值
     * @return 配置值或默认值
     */
    public double getDouble(String path, double defaultValue) {
        Object value = find(path);
        if (value == null) {
            return defaultValue;
        }
        if (!(value instanceof Number)) {
            throw typeError(path, "a number", value);
        }
        return ((Number) value).doubleValue();
    }

    /**
     * 读取布尔值，节点缺失时返回 {@code false}。
     *
     * @param path 点分路径
     * @return 布尔值
     */
    public boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    /**
     * 读取布尔值并支持缺失默认值。
     *
     * @param path 点分路径
     * @param defaultValue 节点缺失时返回值
     * @return 配置值或默认值
     */
    public boolean getBoolean(String path, boolean defaultValue) {
        Object value = find(path);
        if (value == null) {
            return defaultValue;
        }
        if (!(value instanceof Boolean)) {
            throw typeError(path, "a boolean", value);
        }
        return (Boolean) value;
    }

    /**
     * 读取字符串列表，节点缺失时返回空列表。
     *
     * @param path 点分路径
     * @return 不可变字符串列表
     */
    public List<String> getStringList(String path) {
        Object value = find(path);
        if (value == null) {
            return Collections.emptyList();
        }
        if (!(value instanceof List)) {
            throw typeError(path, "a string list", value);
        }
        List<?> raw = (List<?>) value;
        List<String> result = new ArrayList<String>();
        for (Object element : raw) {
            if (!(element instanceof String)) {
                throw typeError(path, "a string list", value);
            }
            result.add((String) element);
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * 按点分路径遍历 YAML 映射。
     *
     * @param path 点分路径
     * @return 节点值；不存在时返回 {@code null}
     */
    private Object find(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("path must not be blank");
        }
        String[] segments = path.split("\\.");
        Object current = root;
        for (String segment : segments) {
            if (!(current instanceof Map)) {
                return null;
            }
            Map<?, ?> map = (Map<?, ?>) current;
            if (!map.containsKey(segment)) {
                return null;
            }
            current = map.get(segment);
        }
        return current;
    }

    /**
     * 创建包含来源文件和配置路径的英文类型异常。
     *
     * @param path 配置路径
     * @param expected 预期类型描述
     * @param actual 实际值
     * @return 类型异常
     */
    private WorkspaceException typeError(String path, String expected, Object actual) {
        return new WorkspaceException("Workspace config value must be " + expected + ": "
                + sourceFile + " [" + path + "], actual=" + actual.getClass().getName());
    }
}
