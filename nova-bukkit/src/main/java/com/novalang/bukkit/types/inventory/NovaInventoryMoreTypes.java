package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 中补充的库存与配方对象类型。 */
public final class NovaInventoryMoreTypes {
    private NovaInventoryMoreTypes() { }
    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaRecipe.class, NovaRecipe::register);
        NovaBukkitRegistrar.register(builder, NovaMerchantRecipe.class, NovaMerchantRecipe::register);
        NovaBukkitRegistrar.register(builder, NovaMerchantInventory.class, NovaMerchantInventory::register);
        NovaBukkitRegistrar.register(builder, NovaFurnaceInventory.class, NovaFurnaceInventory::register);
        NovaBukkitRegistrar.register(builder, NovaMerchant.class, NovaMerchant::register);
        NovaBukkitRegistrar.register(builder, NovaHorseInventory.class, NovaHorseInventory::register);
        NovaBukkitRegistrar.register(builder, NovaEnchantingInventory.class, NovaEnchantingInventory::register);
        NovaBukkitRegistrar.register(builder, NovaCraftingInventory.class, NovaCraftingInventory::register);
        NovaBukkitRegistrar.register(builder, NovaBeaconInventory.class, NovaBeaconInventory::register);
        NovaBukkitRegistrar.register(builder, NovaFireworkEffectMeta.class, NovaFireworkEffectMeta::register);
        NovaBukkitRegistrar.register(builder, NovaKnowledgeBookMeta.class, NovaKnowledgeBookMeta::register);
        NovaBukkitRegistrar.register(builder, NovaEnchantmentOffer.class, NovaEnchantmentOffer::register);
        NovaBukkitRegistrar.register(builder, NovaInventoryType.class, NovaInventoryType::register);
    }
}
