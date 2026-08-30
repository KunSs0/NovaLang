package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

/** Spigot 1.12.2 ItemMeta 扩展；不包含 1.14+ custom model data 或组件 API。 */
final class NovaItemMeta {

    private NovaItemMeta() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableString = JavaTypeRef.javaType(String.class).nullable();
        JavaTypeRef nullableLore = JavaTypeRef.listOf(JavaTypeRef.javaType(String.class)).nullable();
        JavaTypeRef enchants = JavaTypeRef.mapOf(JavaTypeRef.javaType(Enchantment.class), JavaTypeRef.javaType(Integer.class));
        builder.extension(ItemMeta.class, "hasDisplayName", f -> f.returns(Boolean.class).invoke(a -> meta(a).hasDisplayName()));
        builder.extension(ItemMeta.class, "displayName", f -> f.returns(nullableString).invoke(a -> meta(a).getDisplayName()));
        builder.extension(ItemMeta.class, "setDisplayName", f -> f.param("name", nullableString).invoke(a -> { meta(a).setDisplayName(arg(a, 1, String.class)); return null; }));
        builder.extension(ItemMeta.class, "hasLocalizedName", f -> f.returns(Boolean.class).invoke(a -> meta(a).hasLocalizedName()));
        builder.extension(ItemMeta.class, "localizedName", f -> f.returns(nullableString).invoke(a -> meta(a).getLocalizedName()));
        builder.extension(ItemMeta.class, "setLocalizedName", f -> f.param("name", nullableString).invoke(a -> { meta(a).setLocalizedName(arg(a, 1, String.class)); return null; }));
        builder.extension(ItemMeta.class, "hasLore", f -> f.returns(Boolean.class).invoke(a -> meta(a).hasLore()));
        builder.extension(ItemMeta.class, "lore", f -> f.returns(nullableLore).invoke(a -> meta(a).getLore()));
        builder.extension(ItemMeta.class, "setLore", f -> f.param("lore", nullableLore).invoke(a -> { meta(a).setLore(lore(a, 1)); return null; }));
        builder.extension(ItemMeta.class, "enchants", f -> f.returns(enchants).invoke(a -> meta(a).getEnchants()));
        builder.extension(ItemMeta.class, "hasEnchants", f -> f.returns(Boolean.class).invoke(a -> meta(a).hasEnchants()));
        builder.extension(ItemMeta.class, "hasEnchant", f -> f.param("enchantment", Enchantment.class).returns(Boolean.class).invoke(a -> meta(a).hasEnchant(arg(a, 1, Enchantment.class))));
        builder.extension(ItemMeta.class, "getEnchantLevel", f -> f.param("enchantment", Enchantment.class).returns(Integer.class).invoke(a -> meta(a).getEnchantLevel(arg(a, 1, Enchantment.class))));
        builder.extension(ItemMeta.class, "addEnchant", f -> f.param("enchantment", Enchantment.class).param("level", Integer.class).param("ignoreRestrictions", Boolean.class).returns(Boolean.class).invoke(a -> meta(a).addEnchant(arg(a, 1, Enchantment.class), arg(a, 2, Integer.class), arg(a, 3, Boolean.class))));
        builder.extension(ItemMeta.class, "removeEnchant", f -> f.param("enchantment", Enchantment.class).returns(Boolean.class).invoke(a -> meta(a).removeEnchant(arg(a, 1, Enchantment.class))));
        builder.extension(ItemMeta.class, "hasConflictingEnchant", f -> f.param("enchantment", Enchantment.class).returns(Boolean.class).invoke(a -> meta(a).hasConflictingEnchant(arg(a, 1, Enchantment.class))));
        builder.extension(ItemMeta.class, "itemFlags", f -> f.returns(JavaTypeRef.setOf(JavaTypeRef.javaType(ItemFlag.class))).invoke(a -> meta(a).getItemFlags()));
        builder.extension(ItemMeta.class, "hasItemFlag", f -> f.param("flag", ItemFlag.class).returns(Boolean.class).invoke(a -> meta(a).hasItemFlag(arg(a, 1, ItemFlag.class))));
        builder.extension(ItemMeta.class, "addItemFlags", f -> f.param("flag", ItemFlag.class).invoke(a -> { meta(a).addItemFlags(arg(a, 1, ItemFlag.class)); return null; }));
        builder.extension(ItemMeta.class, "removeItemFlags", f -> f.param("flag", ItemFlag.class).invoke(a -> { meta(a).removeItemFlags(arg(a, 1, ItemFlag.class)); return null; }));
        builder.extension(ItemMeta.class, "isUnbreakable", f -> f.returns(Boolean.class).invoke(a -> meta(a).isUnbreakable()));
        builder.extension(ItemMeta.class, "setUnbreakable", f -> f.param("unbreakable", Boolean.class).invoke(a -> { meta(a).setUnbreakable(arg(a, 1, Boolean.class)); return null; }));
        builder.extension(ItemMeta.class, "clone", f -> f.returns(ItemMeta.class).invoke(a -> meta(a).clone()));
    }

    private static ItemMeta meta(Object[] a) {
        return NovaTypeSupport.argument(a, 0, ItemMeta.class);
    }

    @SuppressWarnings("unchecked")
    private static List<String> lore(Object[] a, int index) {
        return (List<String>) NovaTypeSupport.argument(a, index, List.class);
    }

    private static <T> T arg(Object[] a, int index, Class<T> type) {
        return NovaTypeSupport.argument(a, index, type);
    }
}
