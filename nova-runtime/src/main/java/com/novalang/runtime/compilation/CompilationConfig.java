package com.novalang.runtime.compilation;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nova 编译器的可选元数据，只描述路径别名和诊断名称。
 * <p>入口由编译方法单独接收；配置不声明源码目录、入口列表或执行隔离策略。</p>
 */
public final class CompilationConfig {
    private final String name;
    private final Map<String, Path> aliases;

    /** 从构建器复制元数据，保证后续修改不会影响正在编译的程序。 */
    private CompilationConfig(Builder builder) {
        name = builder.name;
        aliases = Collections.unmodifiableMap(new LinkedHashMap<>(builder.aliases));
    }

    /** @return 用于编译诊断的名称 */
    public String getName() {
        return name;
    }

    /** @return 别名到绝对目录的不可变映射 */
    public Map<String, Path> getAliases() {
        return aliases;
    }

    /** @return 复制当前全部元数据的独立构建器 */
    public Builder toBuilder() {
        Builder builder = new Builder();
        builder.name = name;
        builder.aliases.putAll(aliases);
        return builder;
    }

    /** 收集可选编译信息，不扫描文件系统或推断入口。 */
    public static final class Builder {
        private String name = "nova";
        private final Map<String, Path> aliases = new LinkedHashMap<>();

        /** 创建空元数据，单文件及相对 import 无需额外配置。 */
        public Builder() {
        }

        /**
         * 设置诊断名称。
         * @param name 非空且只包含字母、数字、点、下划线或连字符的名称
         * @return 当前构建器
         */
        public Builder name(String name) {
            if (name == null || !name.matches("^[A-Za-z0-9][A-Za-z0-9._-]*$")) {
                throw new CompilationException("Compilation name contains invalid characters: " + name);
            }
            this.name = name;
            return this;
        }

        /**
         * 声明路径别名；相对目录在调用时按宿主工作目录解析。
         * @param alias 以 @ 开头的完整别名
         * @param directory 目标目录；配置加载器会先按 YAML 文件目录解析相对路径
         * @return 当前构建器
         */
        public Builder alias(String alias, Path directory) {
            if (alias == null || !alias.matches("^@(?:[A-Za-z][A-Za-z0-9._-]*)?$")) {
                throw new CompilationException("Alias must start with @ and use a valid name: " + alias);
            }
            if (directory == null) {
                throw new IllegalArgumentException("Alias directory must not be null");
            }
            if (aliases.containsKey(alias)) {
                throw new CompilationException("Duplicate compilation alias: " + alias);
            }
            aliases.put(alias, directory.toAbsolutePath().normalize());
            return this;
        }

        /** @return 当前元数据的不可变快照，允许完全为空 */
        public CompilationConfig build() {
            return new CompilationConfig(this);
        }
    }
}
