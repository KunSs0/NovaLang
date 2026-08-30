package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 中其余实体事件的集中注册入口。 */
public final class NovaEntityFinalEvents {
    private NovaEntityFinalEvents() { }
    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaEntityInteractEvent.class, NovaEntityInteractEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityPortalExitEvent.class, NovaEntityPortalExitEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntitySpawnEvent.class, NovaEntitySpawnEvent::register);
    }
}
