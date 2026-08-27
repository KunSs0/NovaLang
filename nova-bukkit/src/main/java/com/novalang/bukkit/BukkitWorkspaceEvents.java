package com.novalang.bukkit;

import com.novalang.workspace.ExecutionPolicy;
import com.novalang.workspace.NovaCallback;
import com.novalang.workspace.ResourceScope;
import com.novalang.workspace.WorkspaceCallbacks;
import com.novalang.workspace.WorkspaceExecutionContext;
import com.novalang.workspace.WorkspaceResource;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

/**
 * 将 Bukkit 强类型事件监听器登记到当前 Workspace ResourceScope 的通用入口。
 */
public final class BukkitWorkspaceEvents {

    /**
     * 工具类不允许实例化。
     */
    private BukkitWorkspaceEvents() {
    }

    /**
     * 使用 Workspace 默认执行策略订阅 Bukkit 事件。
     *
     * @param eventType Bukkit 事件类型
     * @param priority 监听优先级
     * @param ignoreCancelled 是否忽略已经取消的事件
     * @param entryName Nova 回调所属入口名称
     * @param functionName Nova 回调函数名称
     * @return 已登记到当前作用域的监听资源
     */
    public static WorkspaceResource listen(Class<? extends Event> eventType,
                                           EventPriority priority,
                                           boolean ignoreCancelled,
                                           String entryName,
                                           String functionName) {
        return listen(eventType, priority, ignoreCancelled, entryName, functionName, null);
    }

    /**
     * 使用指定执行策略订阅 Bukkit 事件。
     *
     * @param eventType Bukkit 事件类型
     * @param priority 监听优先级
     * @param ignoreCancelled 是否忽略已经取消的事件
     * @param entryName Nova 回调所属入口名称
     * @param functionName Nova 回调函数名称
     * @param policy 固定执行策略；传 {@code null} 时使用 Workspace 默认策略
     * @return 已登记到当前作用域的监听资源
     */
    public static WorkspaceResource listen(Class<? extends Event> eventType,
                                           EventPriority priority,
                                           boolean ignoreCancelled,
                                           String entryName,
                                           String functionName,
                                           ExecutionPolicy policy) {
        if (eventType == null) {
            throw new IllegalArgumentException("eventType must not be null");
        }
        if (priority == null) {
            throw new IllegalArgumentException("priority must not be null");
        }
        ResourceScope scope = WorkspaceExecutionContext.requireScope();
        NovaCallback callback = WorkspaceCallbacks.create(entryName, functionName, policy);
        NovaBukkitPlugin plugin = NovaBukkitPlugin.requireInstance();
        Subscription subscription = new Subscription(scope, callback);
        EventExecutor executor = (listener, event) -> subscription.invoke(event);
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        // Bukkit 只接收强类型 Class，不解析类名，也不建立反射事件映射。
        pluginManager.registerEvent(eventType, subscription, priority,
                executor, plugin, ignoreCancelled);
        try {
            scope.register(subscription);
            subscription.markRegistered();
            return subscription;
        } catch (RuntimeException exception) {
            subscription.dispose();
            throw exception;
        }
    }

    /**
     * 同时作为 Bukkit Listener 和 Workspace 资源的订阅句柄。
     */
    private static final class Subscription implements Listener, WorkspaceResource {
        private final ResourceScope scope;
        private final NovaCallback callback;
        private boolean registered;
        private boolean disposed;

        Subscription(ResourceScope scope, NovaCallback callback) {
            this.scope = scope;
            this.callback = callback;
        }

        /**
         * 标记监听器已经登记到作用域。
         */
        synchronized void markRegistered() {
            registered = true;
            if (disposed) {
                scope.unregister(this);
            }
        }

        /**
         * 将强类型 Bukkit 事件作为唯一参数传给稳定 Nova 回调。
         *
         * @param event 当前 Bukkit 事件
         */
        void invoke(Event event) {
            synchronized (this) {
                if (disposed || !callback.isValid()) {
                    return;
                }
            }
            callback.invoke(event);
        }

        /**
         * 注销 Bukkit 监听器并解除作用域引用。
         */
        @Override
        public synchronized void dispose() {
            if (disposed) {
                return;
            }
            disposed = true;
            HandlerList.unregisterAll(this);
            if (registered) {
                scope.unregister(this);
            }
        }
    }
}
