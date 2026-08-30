package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** 常用玩家玩法事件注册聚合器。 */
public final class NovaPlayerGameplayEvents {

    private NovaPlayerGameplayEvents() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaPlayerFishEvent.class, NovaPlayerFishEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerGameModeChangeEvent.class, NovaPlayerGameModeChangeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerExpChangeEvent.class, NovaPlayerExpChangeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerLevelChangeEvent.class, NovaPlayerLevelChangeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerKickEvent.class, NovaPlayerKickEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerToggleFlightEvent.class, NovaPlayerToggleFlightEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerToggleSneakEvent.class, NovaPlayerToggleSneakEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerToggleSprintEvent.class, NovaPlayerToggleSprintEvent::register);
        NovaBukkitRegistrar.register(builder, NovaPlayerVelocityEvent.class, NovaPlayerVelocityEvent::register);
    }
}
