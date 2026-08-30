package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.bukkit.types.value.NovaVector;
import com.novalang.bukkit.types.world.NovaChunkSnapshot;
import com.novalang.bukkit.types.world.NovaWorldObject;
import com.novalang.runtime.host.JavaTypes;

/** Bukkit 对象扩展聚合器；各领域实现保持独立注册器。 */
public final class NovaWorldInventory {

    private NovaWorldInventory() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaWorldObject.register(builder);
        NovaInventory.register(builder);
        NovaEntityEquipment.register(builder);
        NovaPlayerInventory.register(builder);
        NovaItemStack.register(builder);
        NovaItemMeta.register(builder);
        NovaItemFactory.register(builder);
        NovaShapedRecipe.register(builder);
        NovaShapelessRecipe.register(builder);
        NovaBukkitRegistrar.register(builder, NovaSkullMeta.class, NovaSkullMeta::register);
        NovaBukkitRegistrar.register(builder, NovaBookMeta.class, NovaBookMeta::register);
        NovaBukkitRegistrar.register(builder, NovaPotionMeta.class, NovaPotionMeta::register);
        NovaBukkitRegistrar.register(builder, NovaFireworkMeta.class, NovaFireworkMeta::register);
        NovaBukkitRegistrar.register(builder, NovaLeatherArmorMeta.class, NovaLeatherArmorMeta::register);
        NovaBukkitRegistrar.register(builder, NovaEnchantmentStorageMeta.class, NovaEnchantmentStorageMeta::register);
        NovaBukkitRegistrar.register(builder, NovaMapMeta.class, NovaMapMeta::register);
        NovaBukkitRegistrar.register(builder, NovaBannerMeta.class, NovaBannerMeta::register);
        NovaBukkitRegistrar.register(builder, NovaSpawnEggMeta.class, NovaSpawnEggMeta::register);
        NovaMaterial.register(builder);
        NovaVector.register(builder);
        NovaChunkSnapshot.register(builder);
    }
}
