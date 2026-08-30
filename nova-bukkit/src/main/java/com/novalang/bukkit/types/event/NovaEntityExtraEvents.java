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
        NovaBukkitRegistrar.register(builder, NovaEntityAirChangeEvent.class, NovaEntityAirChangeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaHorseJumpEvent.class, NovaHorseJumpEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerLeashEntityEvent.class, NovaPlayerLeashEntityEvent::register);
        NovaBukkitRegistrar.register(builder, NovaAreaEffectCloudApplyEvent.class, NovaAreaEffectCloudApplyEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEnderDragonChangePhaseEvent.class, NovaEnderDragonChangePhaseEvent::register);
        NovaBukkitRegistrar.register(builder, NovaItemDespawnEvent.class, NovaItemDespawnEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityToggleGlideEvent.class, NovaEntityToggleGlideEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityPortalEnterEvent.class, NovaEntityPortalEnterEvent::register);
    }
}
