package com.novalang.bukkit;

import com.novalang.runtime.Nova;
import com.novalang.runtime.host.JavaTypes;

/**
 * Bukkit 运行时函数及其编译期 Java 类型定义入口。
 *
 * <p>全局入口与 Fluxon platform-bukkit 保持同一职责边界。Bukkit 原生成员由 Nova
 * 根据真实 {@link Class} 反射解析，Fluxon 的函数别名则通过 JavaTypes 扩展描述显式注册。</p>
 *
 * <p>该基础表以当前模块声明的 Spigot API 1.12.2 为准，不包含新版 Particle、
 * Advancement 或 Paper 专有 API。</p>
 */
public final class BukkitJavaTypes {

    private BukkitJavaTypes() {
    }

    public static JavaTypes create() {
        return builder().build();
    }

    /** 单体 Nova 直接调用；Workspace 可把该方法引用作为 WorkspaceHost。 */
    public static Nova install(Nova nova) {
        if (nova == null) {
            throw new IllegalArgumentException("nova must not be null");
        }
        nova.install(create());
        return nova;
    }

    /** 创建预装 Bukkit API 的 builder，业务插件可继续追加自己的 JavaTypes。 */
    public static JavaTypes.Builder builder() {
        JavaTypes.Builder builder = JavaTypes.builder();
        BukkitServerJavaTypes.register(builder);
        BukkitWorldJavaTypes.register(builder);
        BukkitPlayerJavaTypes.register(builder);
        BukkitLocationJavaTypes.register(builder);
        BukkitEntityJavaTypes.register(builder);
        BukkitValueJavaTypes.register(builder);
        BukkitEnumJavaTypes.register(builder);
        return builder;
    }
}
