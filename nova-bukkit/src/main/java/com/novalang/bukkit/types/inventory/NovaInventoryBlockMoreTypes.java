package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 方块库存接口补充聚合器。 */
public final class NovaInventoryBlockMoreTypes {
    private NovaInventoryBlockMoreTypes() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaDoubleChestInventory.class, NovaDoubleChestInventory::register);
        NovaBukkitRegistrar.register(builder, NovaAbstractHorseInventory.class, NovaAbstractHorseInventory::register);
    }
}
