package com.novalang.bukkit.types.event;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 附魔、熔炉及库存剩余事件注册聚合器。 */
public final class NovaInventoryExtraEvents {
    private NovaInventoryExtraEvents() { }
    public static void register(JavaTypes.Builder b) {
        NovaBukkitRegistrar.register(b, NovaEnchantItemEvent.class, NovaEnchantItemEvent::register);
        NovaBukkitRegistrar.register(b, NovaPrepareItemEnchantEvent.class, NovaPrepareItemEnchantEvent::register);
        NovaBukkitRegistrar.register(b, NovaFurnaceBurnEvent.class, NovaFurnaceBurnEvent::register);
        NovaBukkitRegistrar.register(b, NovaFurnaceExtractEvent.class, NovaFurnaceExtractEvent::register);
        NovaBukkitRegistrar.register(b, NovaBrewingStandFuelEvent.class, NovaBrewingStandFuelEvent::register);
        NovaBukkitRegistrar.register(b, NovaInventoryCreativeEvent.class, NovaInventoryCreativeEvent::register);
        NovaBukkitRegistrar.register(b, NovaInventoryPickupItemEvent.class, NovaInventoryPickupItemEvent::register);
    }
}
