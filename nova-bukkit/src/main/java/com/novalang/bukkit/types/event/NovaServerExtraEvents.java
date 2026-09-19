package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** 其它服务端事件扩展的聚合注册器。 */
public final class NovaServerExtraEvents {

    private NovaServerExtraEvents() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaPortalCreateEvent.class, NovaPortalCreateEvent::register);
    }
}
