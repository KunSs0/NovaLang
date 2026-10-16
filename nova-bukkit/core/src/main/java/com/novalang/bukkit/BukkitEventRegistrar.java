package com.novalang.bukkit;

import com.novalang.runtime.ScriptCallback;
import com.novalang.runtime.ScriptCallbackContexts;
import com.novalang.runtime.ScriptCallbacks;
import com.novalang.runtime.ScriptFunction;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

/**
 * 绑定业务插件的 Bukkit 事件注册器。
 * <p>Nova 调用通过底层 ScriptCallback 捕获绑定和程序生命周期；Java 调用由插件或调用方管理。</p>
 */
public final class BukkitEventRegistrar {
    private final Plugin plugin;

    /** 校验并保存实际 Bukkit 注册所有者。 */
    private BukkitEventRegistrar(Plugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin must not be null");
        }
        this.plugin = plugin;
    }

    /**
     * 创建业务插件的事件注册器。
     * @param plugin Bukkit 注册所有者
     * @return 不持有额外脚本容器的注册器
     */
    public static BukkitEventRegistrar forPlugin(Plugin plugin) {
        return new BukkitEventRegistrar(plugin);
    }

    /**
     * 注册编译期检查过的强类型监听器，Nova 调用自动归属于当前编译程序。
     * @param eventType Bukkit 事件类型
     * @param priority 监听优先级
     * @param ignoreCancelled 是否忽略已取消事件
     * @param listener 事件监听器
     * @return 可主动释放、并随所属程序关闭而自动注销的句柄
     */
    public EventRegistration listen(Class<? extends Event> eventType, EventPriority priority,
                                    boolean ignoreCancelled, BukkitEventListener listener) {
        if (eventType == null || !Event.class.isAssignableFrom(eventType)) {
            throw new IllegalArgumentException("eventType must extend org.bukkit.event.Event");
        }
        if (priority == null || listener == null) {
            throw new IllegalArgumentException("priority and listener must not be null");
        }
        ScriptCallback callback = null;
        if (ScriptCallbackContexts.current() != null) {
            callback = ScriptCallbacks.bind(new ScriptFunction() {
                /** Bukkit 事件回调只接收一个事件。 */
                @Override
                public int argumentCount() {
                    return 1;
                }

                /** 将原始事件直接交给强类型监听器，保留对象身份。 */
                @Override
                public Object invokeArguments(Object... arguments) {
                    listener.handle(eventType.cast(arguments[0]));
                    return null;
                }
            });
        }
        Subscription subscription = new Subscription(listener, callback);
        try {
            EventExecutor executor = (registered, event) -> {
                if (eventType.isInstance(event)) {
                    subscription.invoke(event);
                }
            };
            plugin.getServer().getPluginManager().registerEvent(
                    eventType, subscription, priority, executor, plugin, ignoreCancelled);
            if (callback != null) {
                subscription.bind(callback.register(subscription));
            }
            return subscription;
        } catch (RuntimeException failure) {
            subscription.dispose();
            throw failure;
        }
    }

    /** Bukkit Listener 和 Nova 程序资源共用一个幂等注销句柄。 */
    private static final class Subscription implements Listener, EventRegistration {
        private final BukkitEventListener listener;
        private final ScriptCallback callback;
        private AutoCloseable registration;
        private boolean disposed;

        /** 保存 Java 监听器及可选的 Nova 回调上下文。 */
        private Subscription(BukkitEventListener listener, ScriptCallback callback) {
            this.listener = listener;
            this.callback = callback;
        }

        /** 覆盖注册资源期间程序已经关闭的竞态。 */
        private synchronized void bind(AutoCloseable handle) {
            registration = handle;
            if (disposed) {
                detach();
            }
        }

        /** 校验生命周期后执行，Nova 路径恢复捕获的绑定和类加载环境。 */
        private void invoke(Event event) {
            synchronized (this) {
                if (disposed || (callback != null && !callback.isValid())) {
                    return;
                }
            }
            if (callback != null) {
                callback.invoke(event);
            } else {
                listener.handle(event);
            }
        }

        /** 注销 Bukkit 监听器并解除程序资源登记。 */
        @Override
        public synchronized void dispose() {
            if (disposed) {
                return;
            }
            disposed = true;
            HandlerList.unregisterAll(this);
            detach();
        }

        /** 解除所有者引用，不重复执行资源注销。 */
        private void detach() {
            if (registration == null) {
                return;
            }
            try {
                registration.close();
            } catch (Exception failure) {
                throw new IllegalStateException("Cannot detach Bukkit event registration", failure);
            } finally {
                registration = null;
            }
        }
    }
}
