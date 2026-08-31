package com.novalang.bukkit.types.gameplay;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/** Spigot 1.12.2 enchantment aliases. */
final class NovaEnchantment {

    private NovaEnchantment() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Enchantment.class, "id", f -> f.returns(Integer.class).invoke(a -> enchantment(a).getId()));
        builder.extension(Enchantment.class, "name", f -> f.returns(String.class).invoke(a -> enchantment(a).getName()));
        builder.extension(Enchantment.class, "maxLevel", f -> f.returns(Integer.class).invoke(a -> enchantment(a).getMaxLevel()));
        builder.extension(Enchantment.class, "startLevel", f -> f.returns(Integer.class).invoke(a -> enchantment(a).getStartLevel()));
        builder.extension(Enchantment.class, "itemTarget", f -> f.returns(JavaTypeRef.javaType(EnchantmentTarget.class).nullable()).invoke(a -> enchantment(a).getItemTarget()));
        builder.extension(Enchantment.class, "isTreasure", f -> f.returns(Boolean.class).invoke(a -> enchantment(a).isTreasure()));
        builder.extension(Enchantment.class, "isCursed", f -> f.returns(Boolean.class).invoke(a -> enchantment(a).isCursed()));
        builder.extension(Enchantment.class, "conflictsWith", f -> f.param("enchantment", Enchantment.class).returns(Boolean.class).invoke(a -> enchantment(a).conflictsWith(arg(a, 1, Enchantment.class))));
        builder.extension(Enchantment.class, "canEnchantItem", f -> f.param("item", ItemStack.class).returns(Boolean.class).invoke(a -> enchantment(a).canEnchantItem(arg(a, 1, ItemStack.class))));
        builder.extension(Enchantment.class, "getById", f -> f.param("id", Integer.class).returns(JavaTypeRef.javaType(Enchantment.class).nullable()).invoke(a -> Enchantment.getById(arg(a, 1, Integer.class))));
        builder.extension(Enchantment.class, "getByName", f -> f.param("name", String.class).returns(JavaTypeRef.javaType(Enchantment.class).nullable()).invoke(a -> Enchantment.getByName(arg(a, 1, String.class))));
        builder.extension(Enchantment.class, "values", f -> f.returns(JavaTypeRef.javaType(Enchantment[].class)).invoke(a -> Enchantment.values()));
        builder.extension(EnchantmentTarget.class, "includes", f -> f.param("material", Material.class).returns(Boolean.class)
                .invoke(a -> target(a).includes(arg(a, 1, Material.class))));
        builder.extension(EnchantmentTarget.class, "includes", f -> f.param("material", String.class).returns(Boolean.class)
                .invoke(NovaEnchantment::includesMaterial));
        builder.extension(EnchantmentTarget.class, "includes", f -> f.param("item", ItemStack.class).returns(Boolean.class)
                .invoke(a -> target(a).includes(arg(a, 1, ItemStack.class))));
    }

    private static Enchantment enchantment(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Enchantment.class);
    }

    private static EnchantmentTarget target(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EnchantmentTarget.class);
    }

    private static Boolean includesMaterial(Object[] arguments) {
        Material material = Material.matchMaterial(arg(arguments, 1, String.class));
        if (material == null) {
            return false;
        }
        return target(arguments).includes(material);
    }

    private static <T> T arg(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
