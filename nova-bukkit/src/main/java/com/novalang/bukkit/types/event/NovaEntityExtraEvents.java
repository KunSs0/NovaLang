package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 中其余高价值实体事件的集中注册入口。 */
public final class NovaEntityExtraEvents {
    private NovaEntityExtraEvents() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaSheepDyeWoolEvent.class, NovaSheepDyeWoolEvent::register);
        NovaBukkitRegistrar.register(builder, NovaSheepRegrowWoolEvent.class, NovaSheepRegrowWoolEvent::register);
        NovaBukkitRegistrar.register(builder, NovaVillagerAcquireTradeEvent.class, NovaVillagerAcquireTradeEvent::register);
    }
}
