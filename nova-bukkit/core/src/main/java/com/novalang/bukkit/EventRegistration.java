package com.novalang.bukkit;

/** Bukkit 事件注册资源；可以主动释放，也可以登记到 CompiledNova 统一关闭。 */
public interface EventRegistration extends AutoCloseable {
    /** 幂等注销事件监听器。 */
    void dispose();

    /** 通过标准 AutoCloseable 协议释放监听器。 */
    @Override
    default void close() {
        dispose();
    }
}
