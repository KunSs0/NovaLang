package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;

/** Spigot 1.12.2 附魔报价的 Fluxon 函数别名。 */
public final class NovaEnchantmentOffer {

    private NovaEnchantmentOffer() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EnchantmentOffer.class, "enchantment", function -> function.returns(Enchantment.class)
                .invoke(arguments -> offer(arguments).getEnchantment()));
        builder.extension(EnchantmentOffer.class, "setEnchantment", function -> function.param("enchantment", Enchantment.class).returns(Void.TYPE).invoke(arguments -> {
            offer(arguments).setEnchantment(argument(arguments, 1, Enchantment.class));
            return null;
        }));
        builder.extension(EnchantmentOffer.class, "enchantmentLevel", function -> function.returns(Integer.class)
                .invoke(arguments -> offer(arguments).getEnchantmentLevel()));
        builder.extension(EnchantmentOffer.class, "setEnchantmentLevel", function -> function.param("level", Integer.class).returns(Void.TYPE).invoke(arguments -> {
            offer(arguments).setEnchantmentLevel(argument(arguments, 1, Integer.class));
            return null;
        }));
        builder.extension(EnchantmentOffer.class, "cost", function -> function.returns(Integer.class)
                .invoke(arguments -> offer(arguments).getCost()));
        builder.extension(EnchantmentOffer.class, "setCost", function -> function.param("cost", Integer.class).returns(Void.TYPE).invoke(arguments -> {
            offer(arguments).setCost(argument(arguments, 1, Integer.class));
            return null;
        }));
    }

    private static EnchantmentOffer offer(Object[] arguments) {
        return argument(arguments, 0, EnchantmentOffer.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
