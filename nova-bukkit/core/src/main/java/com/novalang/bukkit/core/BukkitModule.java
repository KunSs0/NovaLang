package com.novalang.bukkit.core;

/**
 * 描述一个可装配到 NovaLang Bukkit 发行包中的版本模块。
 *
 * <p>核心模块只保存模块标识，不引用具体 Bukkit 版本类型；版本模块通过
 * 独立 JAR 提供对应 API 扩展。</p>
 */
public interface BukkitModule {

    /**
     * 返回模块的稳定标识。
     *
     * @return 模块标识
     */
    String id();
}
