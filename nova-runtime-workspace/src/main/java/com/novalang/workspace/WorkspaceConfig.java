package com.novalang.workspace;

import com.novalang.runtime.NovaSecurityPolicy;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 经过强类型校验的 {@code nova.config.yml} 配置。
 */
public final class WorkspaceConfig {

    /**
     * Workspace 支持的安全模式。
     */
    public enum SecurityMode {
        /** 可信服务端脚本，可使用完整 Java 互操作能力。 */
        TRUSTED_SERVER,
        /** Nova 标准安全策略。 */
        STANDARD,
        /** 禁止 Java 互操作的严格安全策略。 */
        STRICT
    }

    private final int version;
    private final String name;
    private final Path configFile;
    private final Map<String, Path> aliases;
    private final List<Path> sourceRoots;
    private final List<String> entries;
    private final SecurityMode securityMode;
    private final ExecutionPolicy executionPolicy;

    /**
     * 创建不可变 Workspace 配置。
     *
     * @param version 配置格式版本
     * @param name Workspace 唯一名称
     * @param configFile 配置文件绝对路径
     * @param aliases Alias 到物理路径的映射
     * @param sourceRoots 允许读取的源码根目录
     * @param entries 入口模块列表
     * @param securityMode 安全模式
     * @param executionPolicy 默认执行策略
     */
    WorkspaceConfig(int version,
                    String name,
                    Path configFile,
                    Map<String, Path> aliases,
                    List<Path> sourceRoots,
                    List<String> entries,
                    SecurityMode securityMode,
                    ExecutionPolicy executionPolicy) {
        this.version = version;
        this.name = name;
        this.configFile = configFile;
        this.aliases = Collections.unmodifiableMap(new LinkedHashMap<String, Path>(aliases));
        this.sourceRoots = Collections.unmodifiableList(new ArrayList<Path>(sourceRoots));
        this.entries = Collections.unmodifiableList(new ArrayList<String>(entries));
        this.securityMode = securityMode;
        this.executionPolicy = executionPolicy;
    }

    /** @return 配置格式版本 */
    public int getVersion() {
        return version;
    }

    /** @return Workspace 名称 */
    public String getName() {
        return name;
    }

    /** @return 配置文件绝对规范路径 */
    public Path getConfigFile() {
        return configFile;
    }

    /** @return 配置文件所在目录 */
    public Path getRootDirectory() {
        return configFile.getParent();
    }

    /** @return 不可变 Alias 映射 */
    public Map<String, Path> getAliases() {
        return aliases;
    }

    /** @return 不可变源码根目录列表 */
    public List<Path> getSourceRoots() {
        return sourceRoots;
    }

    /** @return 不可变入口模块列表 */
    public List<String> getEntries() {
        return entries;
    }

    /** @return 安全模式 */
    public SecurityMode getSecurityMode() {
        return securityMode;
    }

    /** @return 默认执行策略 */
    public ExecutionPolicy getExecutionPolicy() {
        return executionPolicy;
    }

    /**
     * 为当前安全模式创建独立策略实例。
     *
     * @return 新的 Nova 安全策略
     */
    public NovaSecurityPolicy createSecurityPolicy() {
        if (securityMode == SecurityMode.TRUSTED_SERVER) {
            return NovaSecurityPolicy.unrestricted();
        }
        if (securityMode == SecurityMode.STANDARD) {
            return NovaSecurityPolicy.standard();
        }
        return NovaSecurityPolicy.strict();
    }
}
