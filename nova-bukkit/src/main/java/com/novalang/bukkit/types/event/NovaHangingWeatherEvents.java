package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Hanging 与 Weather 事件扩展的聚合注册器。 */
public final class NovaHangingWeatherEvents {

    private NovaHangingWeatherEvents() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaHangingEvent.class, NovaHangingEvent::register);
        NovaBukkitRegistrar.register(builder, NovaHangingBreakEvent.class, NovaHangingBreakEvent::register);
        NovaBukkitRegistrar.register(builder, NovaHangingBreakByEntityEvent.class, NovaHangingBreakByEntityEvent::register);
        NovaBukkitRegistrar.register(builder, NovaHangingPlaceEvent.class, NovaHangingPlaceEvent::register);
        NovaBukkitRegistrar.register(builder, NovaWeatherEvent.class, NovaWeatherEvent::register);
        NovaBukkitRegistrar.register(builder, NovaWeatherChangeEvent.class, NovaWeatherChangeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaThunderChangeEvent.class, NovaThunderChangeEvent::register);
        NovaBukkitRegistrar.register(builder, NovaLightningStrikeEvent.class, NovaLightningStrikeEvent::register);
    }
}
