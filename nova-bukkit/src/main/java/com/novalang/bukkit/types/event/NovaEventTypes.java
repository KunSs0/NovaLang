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
        NovaBukkitRegistrar.register(builder, NovaBlockMultiPlaceEvent.class, NovaBlockMultiPlaceEvent::register);
        NovaBukkitRegistrar.register(builder, NovaChatEvent.class, NovaChatEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityHealthEvent.class, NovaEntityHealthEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerDeathEvent.class, NovaPlayerDeathEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerItemDamageEvent.class, NovaPlayerItemDamageEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerItemBreakEvent.class, NovaPlayerItemBreakEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerRespawnEvent.class, NovaPlayerRespawnEvent::register);
        builder.extension(Event.class, "handlerList", function -> function
                .returns(HandlerList.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Event.class).getHandlers()));
        builder.extension(EventPriority.class, "slot", function -> function
                .returns(Integer.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, EventPriority.class).getSlot()));
    }
}
