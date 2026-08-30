package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

/** 附魔书物品元数据的可选编译期别名。 */
@Requires(classes = {"org.bukkit.inventory.meta.EnchantmentStorageMeta"})
public final class NovaEnchantmentStorageMeta {

    private NovaEnchantmentStorageMeta() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef storedEnchants = JavaTypeRef.mapOf(JavaTypeRef.javaType(Enchantment.class), JavaTypeRef.javaType(Integer.class));
        builder.extension(EnchantmentStorageMeta.class, "hasStoredEnchants", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasStoredEnchants()));
        builder.extension(EnchantmentStorageMeta.class, "hasStoredEnchant", function -> function
                .param("enchantment", Enchantment.class)
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasStoredEnchant(argument(arguments, 1, Enchantment.class))));
        builder.extension(EnchantmentStorageMeta.class, "storedEnchantLevel", function -> function
                .param("enchantment", Enchantment.class)
                .returns(Integer.class)
                .invoke(arguments -> meta(arguments).getStoredEnchantLevel(argument(arguments, 1, Enchantment.class))));
        builder.extension(EnchantmentStorageMeta.class, "storedEnchants", function -> function
                .returns(storedEnchants)
                .invoke(arguments -> meta(arguments).getStoredEnchants()));
        builder.extension(EnchantmentStorageMeta.class, "addStoredEnchant", function -> function
                .param("enchantment", Enchantment.class)
                .param("level", Integer.class)
                .param("ignoreRestrictions", Boolean.class)
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).addStoredEnchant(
                        argument(arguments, 1, Enchantment.class),
                        argument(arguments, 2, Integer.class),
                        argument(arguments, 3, Boolean.class))));
        builder.extension(EnchantmentStorageMeta.class, "removeStoredEnchant", function -> function
                .param("enchantment", Enchantment.class)
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).removeStoredEnchant(argument(arguments, 1, Enchantment.class))));
        builder.extension(EnchantmentStorageMeta.class, "hasConflictingStoredEnchant", function -> function
                .param("enchantment", Enchantment.class)
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasConflictingStoredEnchant(argument(arguments, 1, Enchantment.class))));
        builder.extension(EnchantmentStorageMeta.class, "clone", function -> function
                .returns(EnchantmentStorageMeta.class)
                .invoke(arguments -> meta(arguments).clone()));
    }

    private static EnchantmentStorageMeta meta(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EnchantmentStorageMeta.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
