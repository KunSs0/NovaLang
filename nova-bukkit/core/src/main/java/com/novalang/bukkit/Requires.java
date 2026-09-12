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

    /**
     * 要求存在的公开实例或静态方法，格式为 {@code 完整类名#方法名}。
     *
     * <p>用于同一个 Bukkit 类在不同版本中新增成员的场景。类存在但方法不存在时，
     * 对应注册器不会安装。</p>
     */
    String[] methods() default {};
}
