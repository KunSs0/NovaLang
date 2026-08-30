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
    }
}
