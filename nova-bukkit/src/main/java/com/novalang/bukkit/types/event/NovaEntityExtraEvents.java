package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 中其余高价值实体事件的集中注册入口。 */
public final class NovaEntityExtraEvents {
    private NovaEntityExtraEvents() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaItemMergeEvent.class, NovaItemMergeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityChangeBlockEvent.class, NovaEntityChangeBlockEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityCreatePortalEvent.class, NovaEntityCreatePortalEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityTameEvent.class, NovaEntityTameEvent::register);
        NovaBukkitRegistrar.register(builder, NovaFoodLevelChangeEvent.class, NovaFoodLevelChangeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPigZapEvent.class, NovaPigZapEvent::register);
        NovaBukkitRegistrar.register(builder, NovaSheepDyeWoolEvent.class, NovaSheepDyeWoolEvent::register);
        NovaBukkitRegistrar.register(builder, NovaSheepRegrowWoolEvent.class, NovaSheepRegrowWoolEvent::register);
        NovaBukkitRegistrar.register(builder, NovaSlimeSplitEvent.class, NovaSlimeSplitEvent::register);
        NovaBukkitRegistrar.register(builder, NovaSpawnerSpawnEvent.class, NovaSpawnerSpawnEvent::register);
        NovaBukkitRegistrar.register(builder, NovaVillagerAcquireTradeEvent.class, NovaVillagerAcquireTradeEvent::register);
    }
}
