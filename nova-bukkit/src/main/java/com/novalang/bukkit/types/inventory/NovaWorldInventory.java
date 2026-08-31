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
        NovaFurnaceRecipe.register(builder);
        NovaBukkitRegistrar.register(builder, NovaBlockInventoryHolder.class, NovaBlockInventoryHolder::register);
        NovaBukkitRegistrar.register(builder, NovaInventoryViewModern.class, NovaInventoryViewModern::register);
        NovaBukkitRegistrar.register(builder, NovaChiseledBookshelfInventory.class, NovaChiseledBookshelfInventory::register);
        NovaBukkitRegistrar.register(builder, NovaJukeboxInventory.class, NovaJukeboxInventory::register);
        NovaBukkitRegistrar.register(builder, NovaLecternInventory.class, NovaLecternInventory::register);
        NovaBukkitRegistrar.register(builder, NovaSmithingInventory.class, NovaSmithingInventory::register);
        NovaBukkitRegistrar.register(builder, NovaItemType.class, NovaItemType::register);
        NovaBukkitRegistrar.register(builder, NovaCompassMeta.class, NovaCompassMeta::register);
        NovaBukkitRegistrar.register(builder, NovaCrossbowMeta.class, NovaCrossbowMeta::register);
        NovaBukkitRegistrar.register(builder, NovaBundleMeta.class, NovaBundleMeta::register);
        NovaBukkitRegistrar.register(builder, NovaTropicalFishBucketMeta.class, NovaTropicalFishBucketMeta::register);
        NovaBukkitRegistrar.register(builder, NovaAxolotlBucketMeta.class, NovaAxolotlBucketMeta::register);
        NovaBukkitRegistrar.register(builder, NovaOminousBottleMeta.class, NovaOminousBottleMeta::register);
        NovaBukkitRegistrar.register(builder, NovaSuspiciousStewMeta.class, NovaSuspiciousStewMeta::register);
        NovaBukkitRegistrar.register(builder, NovaMusicInstrument.class, NovaMusicInstrument::register);
        NovaBukkitRegistrar.register(builder, NovaMusicInstrumentMeta.class, NovaMusicInstrumentMeta::register);
        NovaBukkitRegistrar.register(builder, NovaDamageable.class, NovaDamageable::register);
        NovaBukkitRegistrar.register(builder, NovaColorableArmorMeta.class, NovaColorableArmorMeta::register);
        NovaBukkitRegistrar.register(builder, NovaArmorTrim.class, NovaArmorTrim::register);
        NovaBukkitRegistrar.register(builder, NovaTrimMaterial.class, NovaTrimMaterial::register);
        NovaBukkitRegistrar.register(builder, NovaTrimPattern.class, NovaTrimPattern::register);
        NovaBukkitRegistrar.register(builder, NovaBlockDataMeta.class, NovaBlockDataMeta::register);
        NovaBukkitRegistrar.register(builder, NovaArmorMeta.class, NovaArmorMeta::register);
        NovaBukkitRegistrar.register(builder, NovaWritableBookMeta.class, NovaWritableBookMeta::register);
        NovaBukkitRegistrar.register(builder, NovaFoodComponent.class, NovaFoodComponent::register);
        NovaBukkitRegistrar.register(builder, NovaToolComponent.class, NovaToolComponent::register);
        NovaBukkitRegistrar.register(builder, NovaCustomItemTagContainer.class, NovaCustomItemTagContainer::register);
        NovaBukkitRegistrar.register(builder, NovaItemTagAdapterContext.class, NovaItemTagAdapterContext::register);
        NovaBukkitRegistrar.register(builder, NovaItemTagType.class, NovaItemTagType::register);
        NovaBukkitRegistrar.register(builder, NovaDecoratedPotInventory.class, NovaDecoratedPotInventory::register);
        NovaBukkitRegistrar.register(builder, NovaEquipmentSlotGroup.class, NovaEquipmentSlotGroup::register);
        NovaBukkitRegistrar.register(builder, NovaItemCraftResult.class, NovaItemCraftResult::register);
        NovaBukkitRegistrar.register(builder, NovaRecipeChoice.class, NovaRecipeChoice::register);
        NovaBukkitRegistrar.register(builder, NovaSmithingRecipe.class, NovaSmithingRecipe::register);
        NovaBukkitRegistrar.register(builder, NovaSmithingTransformRecipe.class, NovaSmithingTransformRecipe::register);
        NovaBukkitRegistrar.register(builder, NovaSmithingTrimRecipe.class, NovaSmithingTrimRecipe::register);
        NovaBukkitRegistrar.register(builder, NovaStonecuttingRecipe.class, NovaStonecuttingRecipe::register);
        NovaBukkitRegistrar.register(builder, NovaLlamaInventory.class, NovaLlamaInventory::register);
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
