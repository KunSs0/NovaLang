package com.novalang.bukkit.types.gameplay;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.bukkit.types.entity.NovaEntityCombat;
import com.novalang.bukkit.types.entity.NovaEntityObjects;
import com.novalang.runtime.host.JavaTypes;

/** 第二轮游戏对象注册器聚合；由 Bukkit 总入口按需调用。 */
public final class NovaGameplay {
    private NovaGameplay() { }

    public static void register(JavaTypes.Builder builder) {
        NovaAttribute.register(builder);
        NovaEnchantment.register(builder);
        NovaBukkitRegistrar.register(builder, NovaEnchantmentWrapper.class, NovaEnchantmentWrapper::register);
        NovaEffect.register(builder);
        NovaEntityEffect.register(builder);
        NovaPotion.register(builder);
        NovaBukkitRegistrar.register(builder, NovaPotionEffectTypeWrapper.class, NovaPotionEffectTypeWrapper::register);
        NovaProjectileSource.register(builder);
        NovaEntityCombat.register(builder);
        NovaEntityObjects.register(builder);
    }
}
