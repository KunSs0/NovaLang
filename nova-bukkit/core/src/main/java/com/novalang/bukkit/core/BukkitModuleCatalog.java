package com.novalang.bukkit.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取单一发行 JAR 内的 Bukkit 版本模块清单。
 *
 * <p>清单只描述构建时纳入的模块和 API 基线，不执行版本探测，也不改变扩展注册行为。</p>
 */
public final class BukkitModuleCatalog {

    private static final String RESOURCE = "novalang-bukkit-modules.properties";

    private BukkitModuleCatalog() {
    }

    /**
     * 读取模块清单。
     *
     * @return 清单属性；资源不存在时返回空属性集合
     */
    public static Properties load() {
        Properties properties = new Properties();
        InputStream stream = BukkitModuleCatalog.class.getClassLoader().getResourceAsStream(RESOURCE);
        if (stream == null) {
            return properties;
        }
        try (InputStream input = stream) {
            properties.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("无法读取 Bukkit 模块清单", exception);
        }
        return properties;
    }
}
