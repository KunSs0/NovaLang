package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 世界事件别名聚合器。主事件注册器可调用本类的 register。 */
public final class NovaWorldEventTypes {
    private NovaWorldEventTypes() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaWorldEvent.class, NovaWorldEvent::register);
        NovaBukkitRegistrar.register(builder, NovaWorldInitEvent.class, NovaWorldInitEvent::register);
        NovaBukkitRegistrar.register(builder, NovaWorldLoadEvent.class, NovaWorldLoadEvent::register);
        NovaBukkitRegistrar.register(builder, NovaWorldSaveEvent.class, NovaWorldSaveEvent::register);
        NovaBukkitRegistrar.register(builder, NovaWorldUnloadEvent.class, NovaWorldUnloadEvent::register);
        NovaBukkitRegistrar.register(builder, NovaChunkEvent.class, NovaChunkEvent::register);
        NovaBukkitRegistrar.register(builder, NovaChunkLoadEvent.class, NovaChunkLoadEvent::register);
        NovaBukkitRegistrar.register(builder, NovaChunkPopulateEvent.class, NovaChunkPopulateEvent::register);
        NovaBukkitRegistrar.register(builder, NovaChunkUnloadEvent.class, NovaChunkUnloadEvent::register);
        NovaBukkitRegistrar.register(builder, NovaStructureGrowEvent.class, NovaStructureGrowEvent::register);
        NovaBukkitRegistrar.register(builder, NovaSpawnChangeEvent.class, NovaSpawnChangeEvent::register);
    }
}
