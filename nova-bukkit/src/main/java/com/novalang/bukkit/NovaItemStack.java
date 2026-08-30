package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Map;

/** Spigot 1.12.2 ItemStack 扩展。 */
final class NovaItemStack {

    private NovaItemStack() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef item = JavaTypeRef.javaType(ItemStack.class);
        JavaTypeRef nullableMeta = JavaTypeRef.javaType(ItemMeta.class).nullable();
        JavaTypeRef enchants = JavaTypeRef.mapOf(JavaTypeRef.javaType(Enchantment.class), JavaTypeRef.javaType(Integer.class));
        builder.extension(ItemStack.class, "type", f -> f.returns(Material.class).invoke(a -> stack(a).getType()));
        builder.extension(ItemStack.class, "setType", f -> f.param("type", Material.class).invoke(a -> { stack(a).setType(arg(a, 1, Material.class)); return null; }));
        builder.extension(ItemStack.class, "amount", f -> f.returns(Integer.class).invoke(a -> stack(a).getAmount()));
        builder.extension(ItemStack.class, "setAmount", f -> f.param("amount", Integer.class).invoke(a -> { stack(a).setAmount(arg(a, 1, Integer.class)); return null; }));
        builder.extension(ItemStack.class, "data", f -> f.returns(MaterialData.class).invoke(a -> stack(a).getData()));
        builder.extension(ItemStack.class, "setData", f -> f.param("data", MaterialData.class).invoke(a -> { stack(a).setData(arg(a, 1, MaterialData.class)); return null; }));
        builder.extension(ItemStack.class, "durability", f -> f.returns(Integer.class).invoke(a -> (int) stack(a).getDurability()));
        builder.extension(ItemStack.class, "setDurability", f -> f.param("durability", Integer.class).invoke(a -> { stack(a).setDurability(arg(a, 1, Integer.class).shortValue()); return null; }));
        builder.extension(ItemStack.class, "maxStackSize", f -> f.returns(Integer.class).invoke(a -> stack(a).getMaxStackSize()));
        builder.extension(ItemStack.class, "toString", f -> f.returns(String.class).invoke(a -> stack(a).toString()));
        builder.extension(ItemStack.class, "isSimilar", f -> f.param("item", ItemStack.class).returns(Boolean.class).invoke(a -> stack(a).isSimilar(arg(a, 1, ItemStack.class))));
        builder.extension(ItemStack.class, "clone", f -> f.returns(item).invoke(a -> stack(a).clone()));
        builder.extension(ItemStack.class, "containsEnchantment", f -> f.param("enchantment", Enchantment.class).returns(Boolean.class).invoke(a -> stack(a).containsEnchantment(arg(a, 1, Enchantment.class))));
        builder.extension(ItemStack.class, "enchantments", f -> f.returns(enchants).invoke(a -> stack(a).getEnchantments()));
        builder.extension(ItemStack.class, "getEnchantmentLevel", f -> f.param("enchantment", Enchantment.class).returns(Integer.class).invoke(a -> stack(a).getEnchantmentLevel(arg(a, 1, Enchantment.class))));
        builder.extension(ItemStack.class, "addEnchantment", f -> f.param("enchantment", Enchantment.class).param("level", Integer.class).invoke(a -> { stack(a).addEnchantment(arg(a, 1, Enchantment.class), arg(a, 2, Integer.class)); return null; }));
        builder.extension(ItemStack.class, "addEnchantments", f -> f.param("enchantments", enchants).invoke(a -> { stack(a).addEnchantments(map(a, 1)); return null; }));
        builder.extension(ItemStack.class, "addUnsafeEnchantment", f -> f.param("enchantment", Enchantment.class).param("level", Integer.class).invoke(a -> { stack(a).addUnsafeEnchantment(arg(a, 1, Enchantment.class), arg(a, 2, Integer.class)); return null; }));
        builder.extension(ItemStack.class, "addUnsafeEnchantments", f -> f.param("enchantments", enchants).invoke(a -> { stack(a).addUnsafeEnchantments(map(a, 1)); return null; }));
        builder.extension(ItemStack.class, "removeEnchantment", f -> f.param("enchantment", Enchantment.class).returns(Integer.class).invoke(a -> stack(a).removeEnchantment(arg(a, 1, Enchantment.class))));
        builder.extension(ItemStack.class, "serialize", f -> f.returns(JavaTypeRef.mapOf(JavaTypeRef.javaType(String.class), JavaTypeRef.javaType(Object.class))).invoke(a -> stack(a).serialize()));
        builder.extension(ItemStack.class, "deserialize", f -> f.param("map", JavaTypeRef.mapOf(JavaTypeRef.javaType(String.class), JavaTypeRef.javaType(Object.class))).returns(item).invoke(a -> ItemStack.deserialize(serializedMap(a, 1))));
        builder.extension(ItemStack.class, "itemMeta", f -> f.returns(nullableMeta).invoke(a -> stack(a).getItemMeta()));
        builder.extension(ItemStack.class, "hasItemMeta", f -> f.returns(Boolean.class).invoke(a -> stack(a).hasItemMeta()));
        builder.extension(ItemStack.class, "setItemMeta", f -> f.param("meta", nullableMeta).returns(Boolean.class).invoke(a -> stack(a).setItemMeta(arg(a, 1, ItemMeta.class))));
    }

    private static ItemStack stack(Object[] a) {
        return NovaTypeSupport.argument(a, 0, ItemStack.class);
    }

    @SuppressWarnings("unchecked")
    private static Map<Enchantment, Integer> map(Object[] a, int index) {
        return (Map<Enchantment, Integer>) NovaTypeSupport.argument(a, index, Map.class);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> serializedMap(Object[] a, int index) {
        return (Map<String, Object>) NovaTypeSupport.argument(a, index, Map.class);
    }

    private static <T> T arg(Object[] a, int index, Class<T> type) {
        return NovaTypeSupport.argument(a, index, type);
    }
}
