package com.novalang.bukkit;

import com.novalang.runtime.interpreter.JavaInterop;
import com.novalang.workspace.ExecutionPolicy;
import com.novalang.workspace.NovaCallback;
import com.novalang.workspace.ResourceScope;
import com.novalang.workspace.WorkspaceDirectCallback;
import com.novalang.workspace.WorkspaceEventCallback;
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
     * @param eventClassName Bukkit 事件类的全限定名
     * @param priority 监听优先级
     * @param ignoreCancelled 是否忽略已经取消的事件
     * @param entryName Nova 回调所属入口名称
     * @param functionName Nova 回调函数名称
     * @return 已登记到当前作用域的监听资源
     */
    public static WorkspaceResource listen(String eventClassName,
                                           EventPriority priority,
                                           boolean ignoreCancelled,
                                           String entryName,
                                           String functionName) {
        return listen(eventClassName, priority, ignoreCancelled, entryName, functionName, null);
    }

    /**
     * 使用指定执行策略订阅 Bukkit 事件。
     *
     * @param eventClassName Bukkit 事件类的全限定名
     * @param priority 监听优先级
     * @param ignoreCancelled 是否忽略已经取消的事件
     * @param entryName Nova 回调所属入口名称
     * @param functionName Nova 回调函数名称
     * @param policy 固定执行策略；传 {@code null} 时使用 Workspace 默认策略
     * @return 已登记到当前作用域的监听资源
     */
    public static WorkspaceResource listen(String eventClassName,
                                           EventPriority priority,
                                           boolean ignoreCancelled,
                                           String entryName,
                                           String functionName,
                                           ExecutionPolicy policy) {
        Class<? extends Event> eventType = resolveEventType(eventClassName);
        if (priority == null) {
            throw new IllegalArgumentException("priority must not be null");
        }
        ResourceScope scope = WorkspaceExecutionContext.requireScope();
        NovaCallback callback = WorkspaceCallbacks.create(entryName, functionName, policy);
        return register(eventType, priority, ignoreCancelled, scope,
                new NovaCallbackInvocation(callback));
    }

    /**
     * 使用由 Nova 编译类实现的单方法监听器订阅 Bukkit 事件。
     *
     * <p>监听器实例直接持有业务需要的不可变状态，例如副本上下文；事件对象作为
     * {@link BukkitEventListener#handle(Event)} 的唯一参数传入。该入口不创建
     * 函数名回调，也不注入隐式事件绑定。</p>
     *
     * @param eventClassName Bukkit 事件类的全限定名
     * @param priority 监听优先级
     * @param ignoreCancelled 是否忽略已经取消的事件
     * @param listener 由 Nova 字节码类实现的监听器实例
     * @return 已登记到当前作用域的监听资源
     */
    public static WorkspaceResource listen(String eventClassName,
                                           EventPriority priority,
                                           boolean ignoreCancelled,
                                           BukkitEventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        Class<? extends Event> eventType = resolveEventType(eventClassName);
        if (priority == null) {
            throw new IllegalArgumentException("priority must not be null");
        }
        ResourceScope scope = WorkspaceExecutionContext.requireScope();
        WorkspaceDirectCallback callback = WorkspaceCallbacks.createDirect(
                new WorkspaceEventCallback() {
                    @Override
                    public Object invoke(Object value) {
                        listener.handle((Event) value);
                        return null;
                    }
                });
        return register(eventType, priority, ignoreCancelled, scope,
                new DirectListenerInvocation(callback));
    }

    /**
     * 将已经解析的事件类型实际登记到 Bukkit 与当前资源作用域。
     */
    private static WorkspaceResource register(Class<? extends Event> eventType,
                                              EventPriority priority,
                                              boolean ignoreCancelled,
                                              ResourceScope scope,
                                              EventInvocation invocation) {
        NovaBukkitPlugin plugin = NovaBukkitPlugin.requireInstance();
        Subscription subscription = new Subscription(scope, invocation);
        EventExecutor executor = (listener, event) -> {
            if (!eventType.isInstance(event)) {
                return;
            }
            subscription.invoke(event);
        };
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        // 类名只在 Nova 宿主边界解析一次，Bukkit 始终接收经过验证的强类型 Class。
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
     * 使用当前 Workspace 类加载器解析并验证 Bukkit 事件类。
     *
     * @param eventClassName 事件类的全限定名
     * @return 已验证的 Bukkit 事件类
     */
    @SuppressWarnings("unchecked")
    static Class<? extends Event> resolveEventType(String eventClassName) {
        if (eventClassName == null || eventClassName.trim().isEmpty()) {
            throw new IllegalArgumentException("eventClassName must not be blank");
        }
        ClassLoader scriptClassLoader = JavaInterop.getScriptClassLoader();
        if (scriptClassLoader == null) {
            throw new IllegalStateException("No active Nova Workspace class loader");
        }
        Class<?> rawType;
        try {
            rawType = Class.forName(eventClassName, false, scriptClassLoader);
        } catch (ClassNotFoundException exception) {
            throw new IllegalArgumentException("Bukkit event class was not found: " + eventClassName, exception);
        }
        if (!Event.class.isAssignableFrom(rawType)) {
            throw new IllegalArgumentException("Bukkit event class must extend org.bukkit.event.Event: " + eventClassName);
        }
        return (Class<? extends Event>) rawType;
    }

    /**
     * 同时作为 Bukkit Listener 和 Workspace 资源的订阅句柄。
     */
    private static final class Subscription implements Listener, WorkspaceResource {
        private final ResourceScope scope;
        private final EventInvocation invocation;
        private boolean registered;
        private boolean disposed;

        Subscription(ResourceScope scope, EventInvocation invocation) {
            this.scope = scope;
            this.invocation = invocation;
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
                if (disposed || !invocation.isValid()) {
                    return;
                }
            }
            invocation.invoke(event);
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

    /**
     * 统一 Nova 稳定回调与编译监听器实例的生命周期判断和调用。
     */
    private interface EventInvocation {

        boolean isValid();

        void invoke(Event event);
    }

    /**
     * 具名 Workspace 函数回调入口；显式监听器实例不使用该路径。
     */
    private static final class NovaCallbackInvocation implements EventInvocation {
        private final NovaCallback callback;

        NovaCallbackInvocation(NovaCallback callback) {
            this.callback = callback;
        }

        @Override
        public boolean isValid() {
            return callback.isValid();
        }

        @Override
        public void invoke(Event event) {
            callback.invoke(event);
        }
    }

    /**
     * 直接调用 Nova 编译监听器；资源作用域失效后不再进入业务代码。
     */
    private static final class DirectListenerInvocation implements EventInvocation {
        private final WorkspaceDirectCallback callback;

        DirectListenerInvocation(WorkspaceDirectCallback callback) {
            this.callback = callback;
        }

        @Override
        public boolean isValid() {
            return callback.isValid();
        }

        @Override
        public void invoke(Event event) {
            callback.invoke(event);
        }
    }
}
