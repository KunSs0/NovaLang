package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 中未覆盖的实体事件集中注册入口。 */
public final class NovaEntityMoreEvents {
    private NovaEntityMoreEvents() {
    }
    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaEntityCombustByBlockEvent.class, NovaEntityCombustByBlockEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityCombustByEntityEvent.class, NovaEntityCombustByEntityEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityUnleashEvent.class, NovaEntityUnleashEvent::register);
        NovaBukkitRegistrar.register(builder, NovaExpBottleEvent.class, NovaExpBottleEvent::register);
        NovaBukkitRegistrar.register(builder, NovaFireworkExplodeEvent.class, NovaFireworkExplodeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaCreeperPowerEvent.class, NovaCreeperPowerEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityBreakDoorEvent.class, NovaEntityBreakDoorEvent::register);
    }
}
