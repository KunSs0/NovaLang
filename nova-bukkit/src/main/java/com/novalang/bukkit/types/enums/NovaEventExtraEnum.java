package com.novalang.bukkit.types.enums;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.world.PortalCreateEvent;

/** 已注册事件所需的 Spigot 1.12.2 枚举全局函数补充。 */
public final class NovaEventExtraEnum {
    private NovaEventExtraEnum() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaEnum.registerEnum(builder, "creeperPowerEventPowerCause", CreeperPowerEvent.PowerCause.class);
        NovaEnum.registerEnum(builder, "entityUnleashEventUnleashReason", EntityUnleashEvent.UnleashReason.class);
        NovaEnum.registerEnum(builder, "playerResourcePackStatusEventStatus", PlayerResourcePackStatusEvent.Status.class);
        NovaEnum.registerEnum(builder, "portalCreateEventCreateReason", PortalCreateEvent.CreateReason.class);
    }
}
