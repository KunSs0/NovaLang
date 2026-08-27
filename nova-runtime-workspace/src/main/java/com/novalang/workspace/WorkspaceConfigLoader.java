package com.novalang.workspace;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * {@code nova.config.yml} 严格加载器。
 *
 * <p>加载器拒绝未知的枚举值、错误的数据类型及不以 {@code @} 开头的 Alias，避免将
 * 配置错误静默转换为另一种运行行为。</p>
 */
public final class WorkspaceConfigLoader {

    private static final Pattern ALIAS_PATTERN = Pattern.compile("^@(?:[A-Za-z][A-Za-z0-9._-]*)?$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9._-]*$");

    /**
     * 从磁盘读取并校验 Workspace 配置。
     *
     * @param configFile {@code nova.config.yml} 路径
     * @return 强类型不可变配置
     * @throws WorkspaceException 文件不存在、YAML 非法或字段不满足约束时抛出
     */
    public WorkspaceConfig load(Path configFile) {
        if (configFile == null) {
            throw new WorkspaceException("Workspace config path must not be null");
        }

        Path absolute = configFile.toAbsolutePath().normalize();
        if (!Files.isRegularFile(absolute)) {
            throw new WorkspaceException("Workspace config file does not exist: " + absolute);
        }

        Object loaded;
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        Yaml yaml = new Yaml(new SafeConstructor(options));
        try (InputStream input = Files.newInputStream(absolute)) {
            loaded = yaml.load(input);
        } catch (IOException | RuntimeException exception) {
            throw new WorkspaceException("Failed to read Workspace config: " + absolute, exception);
        }

        Map<String, Object> root = requireMap(loaded, "config root");
        int version = requireInteger(root.get("version"), "version");
        if (version != 1) {
            throw new WorkspaceException("Unsupported Workspace config version: " + version);
        }

        String name = requireString(root.get("name"), "name");
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new WorkspaceException("Workspace name contains invalid characters: " + name);
        }

        Path rootDirectory = absolute.getParent();
        Map<String, Path> aliases = parseAliases(root.get("aliases"), rootDirectory);
        List<Path> sourceRoots = parseSourceRoots(root.get("sources"), rootDirectory);
        List<String> entries = parseStringList(root.get("entries"), "entries");
        Map<String, Object> runtime = requireMap(root.get("runtime"), "runtime");
        WorkspaceConfig.SecurityMode security = parseSecurity(requireString(runtime.get("security"), "runtime.security"));
        ExecutionPolicy thread = parseExecutionPolicy(requireString(runtime.get("thread"), "runtime.thread"));

        return new WorkspaceConfig(version, name, absolute, aliases, sourceRoots, entries, security, thread);
    }

    /**
     * 解析并校验 Alias 映射。
     *
     * @param value YAML Alias 节点
     * @param rootDirectory Workspace 根目录
     * @return 保持声明顺序的规范化 Alias 映射
     */
    private Map<String, Path> parseAliases(Object value, Path rootDirectory) {
        Map<String, Object> raw = requireMap(value, "aliases");
        Map<String, Path> aliases = new LinkedHashMap<String, Path>();
        for (Map.Entry<String, Object> entry : raw.entrySet()) {
            String alias = entry.getKey();
            if (!ALIAS_PATTERN.matcher(alias).matches()) {
                throw new WorkspaceException("Alias must start with @ and use a valid name: " + alias);
            }
            String target = requireString(entry.getValue(), "aliases." + alias);
            aliases.put(alias, rootDirectory.resolve(target).toAbsolutePath().normalize());
        }
        return aliases;
    }

    /**
     * 解析相对于 Workspace 根目录的源码根目录。
     *
     * @param value YAML sources 节点
     * @param rootDirectory Workspace 根目录
     * @return 规范化源码根目录列表
     */
    private List<Path> parseSourceRoots(Object value, Path rootDirectory) {
        List<String> values = parseStringList(value, "sources");
        List<Path> roots = new ArrayList<Path>();
        for (String source : values) {
            roots.add(rootDirectory.resolve(source).toAbsolutePath().normalize());
        }
        return roots;
    }

    /**
     * 读取必填的非空字符串列表。
     *
     * @param value YAML 字段值
     * @param field 用于异常信息的字段名
     * @return 字符串列表
     */
    private List<String> parseStringList(Object value, String field) {
        if (!(value instanceof List)) {
            throw new WorkspaceException(field + " must be a non-empty string list");
        }
        List<?> raw = (List<?>) value;
        if (raw.isEmpty()) {
            throw new WorkspaceException(field + " must not be empty");
        }
        List<String> values = new ArrayList<String>();
        for (int index = 0; index < raw.size(); index++) {
            values.add(requireString(raw.get(index), field + "[" + index + "]"));
        }
        return values;
    }

    /**
     * 将配置文本转换为安全模式。
     *
     * @param value runtime.security 文本
     * @return 安全模式
     */
    private WorkspaceConfig.SecurityMode parseSecurity(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        if ("trusted-server".equals(normalized)) {
            return WorkspaceConfig.SecurityMode.TRUSTED_SERVER;
        }
        if ("standard".equals(normalized)) {
            return WorkspaceConfig.SecurityMode.STANDARD;
        }
        if ("strict".equals(normalized)) {
            return WorkspaceConfig.SecurityMode.STRICT;
        }
        throw new WorkspaceException("Unsupported runtime.security: " + value);
    }

    /**
     * 将配置文本转换为固定执行策略。
     *
     * @param value runtime.thread 文本
     * @return 执行策略
     */
    private ExecutionPolicy parseExecutionPolicy(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        if ("main".equals(normalized)) {
            return ExecutionPolicy.MAIN_THREAD;
        }
        if ("caller".equals(normalized)) {
            return ExecutionPolicy.CALLER_THREAD;
        }
        if ("parallel-safe".equals(normalized)) {
            return ExecutionPolicy.PARALLEL_SAFE;
        }
        if ("serial-scope".equals(normalized)) {
            return ExecutionPolicy.SERIAL_SCOPE;
        }
        throw new WorkspaceException("Unsupported runtime.thread: " + value);
    }

    /**
     * 将 YAML 节点转换为字符串键映射。
     *
     * @param value YAML 节点
     * @param field 用于异常信息的字段名
     * @return 保持原顺序的映射
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> requireMap(Object value, String field) {
        if (!(value instanceof Map)) {
            throw new WorkspaceException(field + " must be a map");
        }
        Map<?, ?> raw = (Map<?, ?>) value;
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        for (Map.Entry<?, ?> entry : raw.entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                throw new WorkspaceException(field + " contains a non-string key");
            }
            result.put((String) entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 读取没有小数部分的整数。
     *
     * @param value YAML 字段值
     * @param field 用于异常信息的字段名
     * @return 整数值
     */
    private static int requireInteger(Object value, String field) {
        if (!(value instanceof Number)) {
            throw new WorkspaceException(field + " must be an integer");
        }
        Number number = (Number) value;
        int integer = number.intValue();
        if (number.doubleValue() != integer) {
            throw new WorkspaceException(field + " must be an integer");
        }
        return integer;
    }

    /**
     * 读取并裁剪非空字符串。
     *
     * @param value YAML 字段值
     * @param field 用于异常信息的字段名
     * @return 裁剪后的字符串
     */
    private static String requireString(Object value, String field) {
        if (!(value instanceof String)) {
            throw new WorkspaceException(field + " must be a non-empty string");
        }
        String text = ((String) value).trim();
        if (text.isEmpty()) {
            throw new WorkspaceException(field + " must not be empty");
        }
        return text;
    }
}
