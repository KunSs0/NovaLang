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
        NovaPlayerEvent.register(builder);
        NovaBukkitRegistrar.register(builder, NovaBlockBreakState.class, NovaBlockBreakState::register);
        NovaBukkitRegistrar.register(builder, NovaChatEvent.class, NovaChatEvent::register);
        NovaBukkitRegistrar.register(builder, NovaEntityDamageModifiers.class, NovaEntityDamageModifiers::register);
        NovaBukkitRegistrar.register(builder, NovaEntityPortalEvent.class, NovaEntityPortalEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBroadcastMessageEvent.class, NovaBroadcastMessageEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerLoginResultStrings.class, NovaPlayerLoginResultStrings::register);
        NovaBukkitRegistrar.register(builder, NovaAsyncPlayerPreLoginEvent.class, NovaAsyncPlayerPreLoginEvent::register);
        NovaBukkitRegistrar.register(builder, NovaBlockPhysicsEvent.class, NovaBlockPhysicsEvent::register);
        NovaHangingWeatherEvents.register(builder);
        NovaWorldEventTypes.register(builder);
        NovaPlayerGameplayEvents.register(builder);
        NovaBlockExtraEvents.register(builder);
        NovaEntityExtraEvents.register(builder);
        NovaPlayerExtraEvents.register(builder);
        NovaInventoryExtraEvents.register(builder);
        NovaEntityMoreEvents.register(builder);
        NovaServerExtraEvents.register(builder);
        NovaEntityFinalEvents.register(builder);
        NovaPlayerFinalEvents.register(builder);
        NovaPlayerMissingEvents.register(builder);
        builder.extension(Event.class, "handlerList", function -> function
                .returns(HandlerList.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Event.class).getHandlers()));
        builder.extension(EventPriority.class, "slot", function -> function
                .returns(Integer.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, EventPriority.class).getSlot()));
    }
}
