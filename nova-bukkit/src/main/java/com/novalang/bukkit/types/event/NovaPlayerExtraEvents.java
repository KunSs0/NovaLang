package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** 其余玩家事件扩展的聚合注册器。 */
public final class NovaPlayerExtraEvents {
    private NovaPlayerExtraEvents() { }
    public static void register(JavaTypes.Builder b) {
        NovaBukkitRegistrar.register(b, NovaPlayerBedEnterEvent.class, NovaPlayerBedEnterEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerBedLeaveEvent.class, NovaPlayerBedLeaveEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerBucketEvent.class, NovaPlayerBucketEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerChangedWorldEvent.class, NovaPlayerChangedWorldEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerCommandPreprocessEvent.class, NovaPlayerCommandPreprocessEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerEditBookEvent.class, NovaPlayerEditBookEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerEggThrowEvent.class, NovaPlayerEggThrowEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerLocaleChangeEvent.class, NovaPlayerLocaleChangeEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerPortalEvent.class, NovaPlayerPortalEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerChannelEvent.class, NovaPlayerChannelEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerShearEntityEvent.class, NovaPlayerShearEntityEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerStatisticIncrementEvent.class, NovaPlayerStatisticIncrementEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerItemMendEvent.class, NovaPlayerItemMendEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerArmorStandManipulateEvent.class, NovaPlayerArmorStandManipulateEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerAchievementAwardedEvent.class, NovaPlayerAchievementAwardedEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerPreLoginEvent.class, NovaPlayerPreLoginEvent::register);
    }
}
