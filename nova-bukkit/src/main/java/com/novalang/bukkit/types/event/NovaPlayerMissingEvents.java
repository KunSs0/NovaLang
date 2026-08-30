package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 玩家事件缺口补充聚合器。 */
public final class NovaPlayerMissingEvents {
    private NovaPlayerMissingEvents() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaPlayerChatEvent.class, NovaPlayerChatEvent::register);
    }
}
