package com.novalang.bukkit.core;

import com.novalang.runtime.host.JavaTypes;

/**
 * Bukkit 版本 JavaTypes 扩展模块。
 *
 * <p>Core 模块负责注册跨版本共同成员；版本模块只注册类型本身或方法签名存在差异的成员。</p>
 */
public interface BukkitJavaTypesModule extends BukkitModule {

    /**
     * 将当前版本可用的 JavaTypes 扩展注册到构建器。
     *
     * @param builder JavaTypes 构建器
     */
    void register(JavaTypes.Builder builder);
}
