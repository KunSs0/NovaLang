package com.novalang.bukkit.types.gameplay;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;

/** EnchantmentWrapper 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.enchantments.EnchantmentWrapper"})
public final class NovaEnchantmentWrapper {

    private NovaEnchantmentWrapper() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EnchantmentWrapper.class, "enchantment", function -> function
                .returns(Enchantment.class)
                .invoke(arguments -> wrapper(arguments).getEnchantment()));
    }

    private static EnchantmentWrapper wrapper(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EnchantmentWrapper.class);
    }
}
