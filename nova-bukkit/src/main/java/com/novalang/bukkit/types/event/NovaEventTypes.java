package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;

/** Bukkit 事件基础类型的稳定别名，API 按 Spigot 1.12.2 保持。 */
public final class NovaEventTypes {

    private NovaEventTypes() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBlockEvent.register(builder);
        NovaEntityEvent.register(builder);
        NovaInventoryEvent.register(builder);
        NovaPlayerEvent.register(builder);
        NovaBukkitRegistrar.register(builder, NovaBlockDamageEvent.class, NovaBlockDamageEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockIgniteEvent.class, NovaBlockIgniteEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockMultiPlaceEvent.class, NovaBlockMultiPlaceEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockGrowEvent.class, NovaBlockGrowEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockSpreadEvent.class, NovaBlockSpreadEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockPistonEvent.class, NovaBlockPistonEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockPistonExtendEvent.class, NovaBlockPistonExtendEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockPistonRetractEvent.class, NovaBlockPistonRetractEvent::register);
        NovaBukkitRegistrar.register(builder, NovaSignChangeEvent.class, NovaSignChangeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaChatEvent.class, NovaChatEvent::register);
        NovaBukkitRegistrar.register(builder, NovaCreatureSpawnEvent.class, NovaCreatureSpawnEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityBreedEvent.class, NovaEntityBreedEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityCombustEvent.class, NovaEntityCombustEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityHealthEvent.class, NovaEntityHealthEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityPickupItemEvent.class, NovaEntityPickupItemEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityPortalEvent.class, NovaEntityPortalEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityTargetLivingEvent.class, NovaEntityTargetLivingEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityTeleportEvent.class, NovaEntityTeleportEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerDeathEvent.class, NovaPlayerDeathEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerItemDamageEvent.class, NovaPlayerItemDamageEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerItemBreakEvent.class, NovaPlayerItemBreakEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerInteractEntityEvent.class, NovaPlayerInteractEntityEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerInteractAtEntityEvent.class, NovaPlayerInteractAtEntityEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerItemConsumeEvent.class, NovaPlayerItemConsumeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerItemHeldEvent.class, NovaPlayerItemHeldEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerDropItemEvent.class, NovaPlayerDropItemEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerPickupItemEvent.class, NovaPlayerPickupItemEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerRespawnEvent.class, NovaPlayerRespawnEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBroadcastMessageEvent.class, NovaBroadcastMessageEvent::register);
        NovaBukkitRegistrar.register(builder, NovaMapInitializeEvent.class, NovaMapInitializeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPluginEvent.class, NovaPluginEvent::register);
        NovaBukkitRegistrar.register(builder, NovaServerCommandEvent.class, NovaServerCommandEvent::register);
        NovaBukkitRegistrar.register(builder, NovaServerListPingEvent.class, NovaServerListPingEvent::register);
        NovaBukkitRegistrar.register(builder, NovaServiceEvent.class, NovaServiceEvent::register);
        NovaBukkitRegistrar.register(builder, NovaTabCompleteEvent.class, NovaTabCompleteEvent::register);
        NovaBukkitRegistrar.register(builder, NovaVehicleEvent.class, NovaVehicleEvent::register);
        NovaBukkitRegistrar.register(builder, NovaVehicleDamageEvent.class, NovaVehicleDamageEvent::register);
        NovaBukkitRegistrar.register(builder, NovaVehicleDestroyEvent.class, NovaVehicleDestroyEvent::register);
        NovaBukkitRegistrar.register(builder, NovaVehiclePassengerEvent.class, NovaVehiclePassengerEvent::register);
        NovaBukkitRegistrar.register(builder, NovaVehicleMoveEvent.class, NovaVehicleMoveEvent::register);
        NovaBukkitRegistrar.register(builder, NovaVehicleCollisionEvent.class, NovaVehicleCollisionEvent::register);
        NovaBukkitRegistrar.register(builder, NovaInventoryMoveItemEvent.class, NovaInventoryMoveItemEvent::register);
        NovaBukkitRegistrar.register(builder, NovaCraftItemEvent.class, NovaCraftItemEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPrepareItemCraftEvent.class, NovaPrepareItemCraftEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBrewEvent.class, NovaBrewEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPrepareAnvilEvent.class, NovaPrepareAnvilEvent::register);
        NovaBukkitRegistrar.register(builder, NovaProjectileLaunchEvent.class, NovaProjectileLaunchEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityShootBowEvent.class, NovaEntityShootBowEvent::register);
        NovaBukkitRegistrar.register(builder, NovaExplosionPrimeEvent.class, NovaExplosionPrimeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPotionSplashEvent.class, NovaPotionSplashEvent::register);
        NovaBukkitRegistrar.register(builder, NovaLingeringPotionSplashEvent.class, NovaLingeringPotionSplashEvent::register);
        NovaBukkitRegistrar.register(builder, NovaItemSpawnEvent.class, NovaItemSpawnEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerLoginEvent.class, NovaPlayerLoginEvent::register);
        NovaBukkitRegistrar.register(builder, NovaAsyncPlayerPreLoginEvent.class, NovaAsyncPlayerPreLoginEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerSwapHandItemsEvent.class, NovaPlayerSwapHandItemsEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockPhysicsEvent.class, NovaBlockPhysicsEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockFadeEvent.class, NovaBlockFadeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockExplodeEvent.class, NovaBlockExplodeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockDispenseEvent.class, NovaBlockDispenseEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockBurnEvent.class, NovaBlockBurnEvent::register);
        NovaBukkitRegistrar.register(builder, NovaNotePlayEvent.class, NovaNotePlayEvent::register);
        NovaHangingWeatherEvents.register(builder);
        NovaWorldEventTypes.register(builder);
        NovaPlayerGameplayEvents.register(builder);
        builder.extension(Event.class, "handlerList", function -> function
                .returns(HandlerList.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Event.class).getHandlers()));
        builder.extension(EventPriority.class, "slot", function -> function
                .returns(Integer.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, EventPriority.class).getSlot()));
    }
}
