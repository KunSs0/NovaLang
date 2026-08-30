package com.novalang.bukkit.types.enums;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

/** Bukkit 事件原因的 Spigot 1.12.2 Fluxon 枚举入口。 */
final class NovaEventEnum {

    private NovaEventEnum() {
    }

    static void register(JavaTypes.Builder builder) {
        NovaEnum.registerEnum(builder, "blockIgniteEventIgniteCause", BlockIgniteEvent.IgniteCause.class);
        NovaEnum.registerEnum(builder, "entityDamageEventDamageModifier", EntityDamageEvent.DamageModifier.class);
        NovaEnum.registerEnum(builder, "entityRegainHealthEventRegainReason", EntityRegainHealthEvent.RegainReason.class);
        NovaEnum.registerEnum(builder, "hangingBreakEventRemoveCause", HangingBreakEvent.RemoveCause.class);
        NovaEnum.registerEnum(builder, "playerPreLoginEventResult", PlayerPreLoginEvent.Result.class);
    }
}
