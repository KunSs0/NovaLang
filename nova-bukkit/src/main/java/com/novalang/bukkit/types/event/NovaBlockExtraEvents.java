package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 剩余方块事件注册聚合器。 */
public final class NovaBlockExtraEvents {

    private NovaBlockExtraEvents() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaBlockFormEvent.class, NovaBlockFormEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockCanBuildEvent.class, NovaBlockCanBuildEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockExpEvent.class, NovaBlockExpEvent::register);
    }
}
