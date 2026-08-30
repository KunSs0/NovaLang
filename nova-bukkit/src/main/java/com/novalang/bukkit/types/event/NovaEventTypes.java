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
        NovaBukkitRegistrar.register(builder, NovaEntityHealthEvent.class, NovaEntityHealthEvent::register);
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
        builder.extension(Event.class, "handlerList", function -> function
                .returns(HandlerList.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Event.class).getHandlers()));
        builder.extension(EventPriority.class, "slot", function -> function
                .returns(Integer.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, EventPriority.class).getSlot()));
    }
}
