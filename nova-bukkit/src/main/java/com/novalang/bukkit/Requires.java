package com.novalang.bukkit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明 Bukkit 类型注册器依赖的运行时类。
 *
 * <p>要求使用类名而非 {@code Class} 字面量，避免缺失的可选 API 在读取注解前就触发链接。
 * 被 {@link NovaBukkitRegistrar} 调用的注册器只有在全部类都存在时才会执行。</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Requires {

    String[] classes();
}
