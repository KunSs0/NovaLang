package com.novalang.bukkit;

import org.bukkit.event.Event;

/**
 * 由 Nova 编译类直接实现的 Bukkit 事件回调。
 *
 * <p>该接口保留唯一的 {@link #handle(Event)} 方法，使 Nova 可以生成普通 JVM
 * 字节码类并直接接收 Bukkit 事件，不需要通过函数名查找或调用期绑定传递事件对象。</p>
 */
@FunctionalInterface
public interface BukkitEventListener {

    /**
     * 处理当前 Bukkit 事件。
     *
     * @param event 已按订阅事件类型验证的 Bukkit 事件
     */
    void handle(Event event);
}
