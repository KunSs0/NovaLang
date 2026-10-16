package com.novalang.runtime.compilation;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * {@code nova.config.yml} 严格加载器。
 *
 * <p>加载器拒绝未知的枚举值、错误的数据类型及不以 {@code @} 开头的 Alias，避免将
 * 配置错误静默转换为另一种运行行为。</p>
 */
public final class CompilationConfigLoader {

    private static final Pattern ALIAS_PATTERN = Pattern.compile("^@(?:[A-Za-z][A-Za-z0-9._-]*)?$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9._-]*$");

    /**
     * 从磁盘读取并校验 Compilation 配置。
     *
     * @param configFile {@code nova.config.yml} 路径
     * @return 强类型不可变配置
     * @throws CompilationException 文件不存在、YAML 非法或字段不满足约束时抛出
     */
    public CompilationConfig load(Path configFile) {
        if (configFile == null) {
            throw new CompilationException("Compilation config path must not be null");
        }

        Path absolute = configFile.toAbsolutePath().normalize();
        if (!Files.isRegularFile(absolute)) {
            throw new CompilationException("Compilation config file does not exist: " + absolute);
        }

        Object loaded;
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        Yaml yaml = new Yaml(new SafeConstructor(options));
        try (InputStream input = Files.newInputStream(absolute)) {
            loaded = yaml.load(input);
        } catch (IOException | RuntimeException exception) {
            throw new CompilationException("Failed to read Compilation config: " + absolute, exception);
        }

        Map<String, Object> root = requireMap(loaded, "config root");
        // test 属于同一项目文件的独立测试配置，其余根字段必须是明确的编译元数据。
        for (String field : root.keySet()) {
            if (!"version".equals(field) && !"name".equals(field) && !"aliases".equals(field)
                    && !"test".equals(field) && !"runtime".equals(field)) {
                throw new CompilationException("Unknown compiler metadata field: " + field);
            }
        }
        int version = requireInteger(root.get("version"), "version");
        if (version != 1) {
            throw new CompilationException("Unsupported Compilation config version: " + version);
        }

        String name = root.containsKey("name") ? requireString(root.get("name"), "name") : "nova";
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new CompilationException("Compilation name contains invalid characters: " + name);
        }

        Path rootDirectory = absolute.getParent();
        Map<String, Path> aliases = new LinkedHashMap<>();
        if (root.containsKey("aliases")) {
            aliases = parseAliases(root.get("aliases"), rootDirectory);
        }
        if (root.containsKey("runtime")) {
            throw new CompilationException("runtime is not compiler metadata; configure Nova and the host explicitly");
        }
        CompilationConfig.Builder builder = new CompilationConfig.Builder();
        builder.name(name);
        for (Map.Entry<String, Path> alias : aliases.entrySet()) {
            builder.alias(alias.getKey(), alias.getValue());
        }
        return builder.build();
    }

    /**
     * 解析并校验 Alias 映射。
     *
     * @param value YAML Alias 节点
     * @param rootDirectory Compilation 根目录
     * @return 保持声明顺序的规范化 Alias 映射
     */
    private Map<String, Path> parseAliases(Object value, Path rootDirectory) {
        Map<String, Object> raw = requireMap(value, "aliases");
        Map<String, Path> aliases = new LinkedHashMap<String, Path>();
        for (Map.Entry<String, Object> entry : raw.entrySet()) {
            String alias = entry.getKey();
            if (!ALIAS_PATTERN.matcher(alias).matches()) {
                throw new CompilationException("Alias must start with @ and use a valid name: " + alias);
            }
            String target = requireString(entry.getValue(), "aliases." + alias);
            aliases.put(alias, rootDirectory.resolve(target).toAbsolutePath().normalize());
        }
        return aliases;
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
            throw new CompilationException(field + " must be a map");
        }
        Map<?, ?> raw = (Map<?, ?>) value;
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        for (Map.Entry<?, ?> entry : raw.entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                throw new CompilationException(field + " contains a non-string key");
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
            throw new CompilationException(field + " must be an integer");
        }
        Number number = (Number) value;
        int integer = number.intValue();
        if (number.doubleValue() != integer) {
            throw new CompilationException(field + " must be an integer");
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
            throw new CompilationException(field + " must be a non-empty string");
        }
        String text = ((String) value).trim();
        if (text.isEmpty()) {
            throw new CompilationException(field + " must not be empty");
        }
        return text;
    }
}
