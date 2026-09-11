package com.novalang.bukkit;

import com.novalang.runtime.interpreter.JavaInterop;
import com.novalang.workspace.ResourceScope;
import com.novalang.workspace.WorkspaceCallbacks;
import com.novalang.workspace.WorkspaceDirectCallback;
import com.novalang.workspace.WorkspaceEventCallback;
import com.novalang.workspace.WorkspaceExecutionContext;
import com.novalang.workspace.WorkspaceGeneration;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Bukkit 事件注册器。
 *
 * <p>注册所有者始终是构造器传入的业务插件。注册调用发生在 Workspace 执行上下文中
 * 时，回调会捕获当前 Generation、Scope 和绑定快照；否则回调直接在 Bukkit 调用线程
 * 执行。</p>
 */
public final class BukkitEventRegistrar {

    private final Plugin plugin;
    private final RegistrationMode mode;
    private final WorkspaceGeneration boundGeneration;
    private final ResourceScope boundScope;
    private final ClassLoader pluginClassLoader;

    private BukkitEventRegistrar(Plugin plugin,
                                 RegistrationMode mode,
                                 WorkspaceGeneration boundGeneration,
                                 ResourceScope boundScope) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin must not be null");
        }
        this.plugin = plugin;
        this.mode = mode;
        this.boundGeneration = boundGeneration;
        this.boundScope = boundScope;
        this.pluginClassLoader = plugin.getClass().getClassLoader();
        if (pluginClassLoader == null) {
            throw new IllegalArgumentException("plugin must be defined by a non-bootstrap ClassLoader");
        }
    }

    /**
     * 创建绑定 Bukkit Plugin 的注册器。
     *
     * @param plugin Bukkit 注册所有者
     * @return standalone 注册器；若调用时存在 Workspace 上下文则自动捕获该上下文
     */
    public static BukkitEventRegistrar forPlugin(Plugin plugin) {
        return new BukkitEventRegistrar(plugin, RegistrationMode.CONTEXTUAL, null, null);
    }

    /**
     * 创建绑定具体 ResourceScope 的 Workspace 注册器。
     *
     * @param plugin Bukkit 注册所有者
     * @param scope 事件资源所属作用域；注册时必须处于该 Scope 的执行上下文
     * @return Workspace 注册器
     */
    public static BukkitEventRegistrar forWorkspace(Plugin plugin, ResourceScope scope) {
        if (scope == null) {
            throw new IllegalArgumentException("scope must not be null");
        }
        return new BukkitEventRegistrar(plugin, RegistrationMode.BOUND_SCOPE, null, scope);
    }

    /**
     * 创建可在 Workspace 执行上下文之外使用的显式注册器。
     */
    public static BukkitEventRegistrar forWorkspace(Plugin plugin,
                                                    WorkspaceGeneration generation,
                                                    ResourceScope scope) {
        if (generation == null) {
            throw new IllegalArgumentException("generation must not be null");
        }
        if (scope == null) {
            throw new IllegalArgumentException("scope must not be null");
        }
        return new BukkitEventRegistrar(plugin, RegistrationMode.BOUND_GENERATION,
                generation, scope);
    }

    /**
     * 注册一个编译期检查过的 Bukkit 监听器。
     *
     * @param eventClassName Bukkit 事件全限定名
     * @param priority 监听优先级
     * @param ignoreCancelled 是否忽略已取消事件
     * @param listener 强类型事件监听器
     * @return 可手动或自动释放的注册句柄
     */
    public EventRegistration listen(String eventClassName,
                                    EventPriority priority,
                                    boolean ignoreCancelled,
                                    BukkitEventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener must not be null");
        }
        if (priority == null) {
            throw new IllegalArgumentException("priority must not be null");
        }
        Class<? extends Event> eventType = resolveBoundEventType(eventClassName);
        ResourceScope scope = boundScope;
        if (mode == RegistrationMode.CONTEXTUAL) {
            scope = WorkspaceExecutionContext.currentScope();
        }
        if (mode == RegistrationMode.CONTEXTUAL && scope == null) {
            return registerStandalone(eventType, priority, ignoreCancelled, listener);
        }
        ResourceScope currentScope = WorkspaceExecutionContext.currentScope();
        if (mode == RegistrationMode.BOUND_SCOPE && currentScope != boundScope) {
            throw new IllegalStateException("The current Workspace scope does not match the registrar scope");
        }
        WorkspaceDirectCallback callback;
        if (mode == RegistrationMode.BOUND_GENERATION) {
            callback = boundGeneration.createDirectCallback(scope, new WorkspaceEventCallback() {
                @Override
                public Object invoke(Object value) {
                    listener.handle((Event) value);
                    return null;
                }
            });
        } else {
            if (WorkspaceExecutionContext.currentGeneration() == null) {
                throw new IllegalStateException("A Workspace Generation is required for Workspace event registration");
            }
            callback = WorkspaceCallbacks.createDirect(new WorkspaceEventCallback() {
                @Override
                public Object invoke(Object value) {
                    listener.handle((Event) value);
                    return null;
                }
            });
        }
        return registerWorkspace(eventType, priority, ignoreCancelled, scope, callback);
    }

    /**
     * 使用当前脚本/代际 ClassLoader 解析事件类，并验证其继承 Bukkit Event。
     */
    @SuppressWarnings("unchecked")
    static Class<? extends Event> resolveEventType(String eventClassName) {
        return resolveEventType(eventClassName, JavaInterop.getScriptClassLoader());
    }

    private Class<? extends Event> resolveBoundEventType(String eventClassName) {
        ClassLoader classLoader = JavaInterop.getScriptClassLoader();
        if (classLoader == null && boundGeneration != null) {
            classLoader = boundGeneration.getScriptClassLoader();
        }
        if (classLoader == null) {
            classLoader = pluginClassLoader;
        }
        return resolveEventType(eventClassName, classLoader);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Event> resolveEventType(String eventClassName,
                                                           ClassLoader classLoader) {
        if (eventClassName == null || eventClassName.trim().isEmpty()) {
            throw new IllegalArgumentException("eventClassName must not be blank");
        }
        if (classLoader == null) {
            throw new IllegalStateException("No active Nova script class loader");
        }
        Class<?> rawType;
        try {
            rawType = Class.forName(eventClassName, false, classLoader);
        } catch (ClassNotFoundException exception) {
            throw new IllegalArgumentException("Bukkit event class was not found: " + eventClassName, exception);
        }
        if (!Event.class.isAssignableFrom(rawType)) {
            throw new IllegalArgumentException(
                    "Bukkit event class must extend org.bukkit.event.Event: " + eventClassName);
        }
        return (Class<? extends Event>) rawType;
    }

    private EventRegistration registerStandalone(Class<? extends Event> eventType,
                                                 EventPriority priority,
                                                 boolean ignoreCancelled,
                                                 BukkitEventListener listener) {
        Subscription subscription = new Subscription(null, listener, null, eventType);
        registerWithBukkit(eventType, priority, ignoreCancelled, subscription);
        subscription.markRegistered();
        return subscription;
    }

    private EventRegistration registerWorkspace(Class<? extends Event> eventType,
                                                EventPriority priority,
                                                boolean ignoreCancelled,
                                                ResourceScope scope,
                                                WorkspaceDirectCallback callback) {
        Subscription subscription = new Subscription(scope, null, callback, eventType);
        try {
            registerWithBukkit(eventType, priority, ignoreCancelled, subscription);
            scope.register(subscription);
            subscription.markRegistered();
            return subscription;
        } catch (RuntimeException exception) {
            subscription.dispose();
            throw exception;
        }
    }

    private void registerWithBukkit(Class<? extends Event> eventType,
                                    EventPriority priority,
                                    boolean ignoreCancelled,
                                    Subscription subscription) {
        EventExecutor executor = (registeredListener, event) -> {
            if (!eventType.isInstance(event)) {
                return;
            }
            subscription.invoke(event);
        };
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvent(eventType, subscription, priority,
                executor, plugin, ignoreCancelled);
    }

    private enum RegistrationMode {
        CONTEXTUAL,
        BOUND_SCOPE,
        BOUND_GENERATION
    }

    /** Bukkit Listener 与 WorkspaceResource 的统一句柄。 */
    private static final class Subscription implements Listener, EventRegistration {
        private final ResourceScope scope;
        private final BukkitEventListener standaloneListener;
        private final WorkspaceDirectCallback workspaceCallback;
        private final Class<? extends Event> eventType;
        private boolean registered;
        private boolean disposed;

        private Subscription(ResourceScope scope,
                             BukkitEventListener standaloneListener,
                             WorkspaceDirectCallback workspaceCallback,
                             Class<? extends Event> eventType) {
            this.scope = scope;
            this.standaloneListener = standaloneListener;
            this.workspaceCallback = workspaceCallback;
            this.eventType = eventType;
        }

        private synchronized void markRegistered() {
            registered = true;
            if (disposed && scope != null) {
                scope.unregister(this);
            }
        }

        private void invoke(Event event) {
            synchronized (this) {
                if (disposed) {
                    return;
                }
                if (workspaceCallback != null && !workspaceCallback.isValid()) {
                    return;
                }
            }
            if (workspaceCallback != null) {
                workspaceCallback.invoke(event);
                return;
            }
            standaloneListener.handle(eventType.cast(event));
        }

        @Override
        public synchronized void dispose() {
            if (disposed) {
                return;
            }
            disposed = true;
            HandlerList.unregisterAll(this);
            if (registered && scope != null) {
                scope.unregister(this);
            }
        }
    }
}
