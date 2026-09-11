package com.novalang.bukkit;

import com.novalang.runtime.Nova;
import com.novalang.runtime.host.JavaTypeRefs;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

/**
 * NoBukkit 脚本门面。
 *
 * <p>实例由宿主绑定实际业务 Plugin 后安装到 Nova。脚本只访问
 * {@code NoBukkit.event.listen(...)}，不接触注册器或 Workspace 控制对象。</p>
 */
public final class NoBukkit {

    private final EventApi event;

    NoBukkit(Plugin plugin) {
        event = new EventApi(BukkitEventRegistrar.forPlugin(plugin));
    }

    /** 返回事件命名空间。 */
    public EventApi getEvent() {
        return event;
    }

    /**
     * 为 Nova 创建绑定业务 Plugin 的 NoBukkit 类型描述。
     */
    public static JavaTypes create(Plugin plugin) {
        JavaTypes.Builder builder = JavaTypes.builder();
        register(builder, plugin);
        return builder.build();
    }

    /** 向已有 JavaTypes 构建器注册绑定后的 NoBukkit 对象。 */
    static void register(JavaTypes.Builder builder, Plugin plugin) {
        if (builder == null) {
            throw new IllegalArgumentException("builder must not be null");
        }
        NoBukkit binding = new NoBukkit(plugin);
        builder.globalObject("NoBukkit", object -> object
                        .type(NoBukkit.class)
                        .value(binding)
                        .object("event", event -> event
                                .type(EventApi.class)
                                .function("listen", function -> function
                                        .param("eventClassName", JavaTypeRefs.STRING)
                                        .param("priority", EventPriority.class)
                                        .param("ignoreCancelled", JavaTypeRefs.BOOLEAN)
                                        .param("listener", BukkitEventListener.class)
                                        .returns(EventRegistration.class))));
    }

    /**
     * 将绑定实际业务 Plugin 的 NoBukkit API 安装到 Nova。
     */
    public static Nova install(Nova nova, Plugin plugin) {
        if (nova == null) {
            throw new IllegalArgumentException("nova must not be null");
        }
        nova.install(create(plugin));
        return nova;
    }

    /** NoBukkit.event 命名空间的运行时对象。 */
    public static final class EventApi {
        private final BukkitEventRegistrar registrar;

        private EventApi(BukkitEventRegistrar registrar) {
            this.registrar = registrar;
        }

        /**
         * 注册 Bukkit 事件监听器。
         */
        public EventRegistration listen(String eventClassName,
                                         EventPriority priority,
                                         boolean ignoreCancelled,
                                         BukkitEventListener listener) {
            return registrar.listen(eventClassName, priority, ignoreCancelled, listener);
        }
    }
}
