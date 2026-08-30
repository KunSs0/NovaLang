package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** 1.12.2 库存与物品元数据补充扩展聚合器。 */
public final class NovaInventoryMetaMoreTypes {
    private NovaInventoryMetaMoreTypes() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaAnvilInventory.class, NovaAnvilInventory::register);
        NovaBukkitRegistrar.register(builder, NovaBrewerInventory.class, NovaBrewerInventory::register);
        NovaBukkitRegistrar.register(builder, NovaRepairable.class, NovaRepairable::register);
        NovaBukkitRegistrar.register(builder, NovaBlockStateMeta.class, NovaBlockStateMeta::register);
        NovaBukkitRegistrar.register(builder, NovaBannerMetaMoreTypes.class, NovaBannerMetaMoreTypes::register);
    }
}
