package com.novalang.bukkit.types.enums;
import com.novalang.bukkit.Requires; import com.novalang.runtime.host.JavaTypes; import org.bukkit.enchantments.EnchantmentTarget;
@Requires(classes = {"org.bukkit.enchantments.EnchantmentTarget"}) public final class NovaEnchantmentEnum { private NovaEnchantmentEnum() { } public static void register(JavaTypes.Builder b) { NovaEnum.registerEnum(b,"enchantmentTarget",EnchantmentTarget.class); } }
