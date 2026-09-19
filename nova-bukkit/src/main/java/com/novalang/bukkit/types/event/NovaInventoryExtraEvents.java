package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 附魔、熔炉及库存剩余事件注册聚合器。 */
public final class NovaInventoryExtraEvents {
    private NovaInventoryExtraEvents() { }
    public static void register(JavaTypes.Builder b) {
        NovaBukkitRegistrar.register(b, NovaPrepareItemEnchantEvent.class, NovaPrepareItemEnchantEvent::register);
    }
}
