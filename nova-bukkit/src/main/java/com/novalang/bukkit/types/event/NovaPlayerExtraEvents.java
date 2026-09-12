package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** 其余玩家事件扩展的聚合注册器。 */
public final class NovaPlayerExtraEvents {
    private NovaPlayerExtraEvents() { }
    public static void register(JavaTypes.Builder b) {
        NovaBukkitRegistrar.register(b, NovaPlayerPortalEvent.class, NovaPlayerPortalEvent::register);
        NovaBukkitRegistrar.register(b, NovaPlayerAchievementAwardedEvent.class, NovaPlayerAchievementAwardedEvent::register);
    }
}
