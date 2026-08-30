package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 玩家事件的补充注册器。 */
public final class NovaPlayerFinalEvents {

    private NovaPlayerFinalEvents() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaPlayerAnimationEvent.class, NovaPlayerAnimationEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerChatTabCompleteEvent.class, NovaPlayerChatTabCompleteEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerAdvancementDoneEvent.class, NovaPlayerAdvancementDoneEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerChangedMainHandEvent.class, NovaPlayerChangedMainHandEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerResourcePackStatusEvent.class, NovaPlayerResourcePackStatusEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerUnleashEntityEvent.class, NovaPlayerUnleashEntityEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerPickupArrowEvent.class, NovaPlayerPickupArrowEvent::register);
    }
}
