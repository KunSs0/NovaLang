package com.novalang.bukkit.types.gameplay;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.bukkit.types.entity.NovaEntityCombat;
import com.novalang.runtime.host.JavaTypes;

/** 第二轮游戏对象注册器聚合；由 Bukkit 总入口按需调用。 */
public final class NovaGameplay {
    private NovaGameplay() { }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaEnchantmentWrapper.class, NovaEnchantmentWrapper::register);
        NovaEntityEffect.register(builder);
        NovaBukkitRegistrar.register(builder, NovaPotionEffectTypeWrapper.class, NovaPotionEffectTypeWrapper::register);
        NovaProjectileSource.register(builder);
        NovaBukkitRegistrar.register(builder, NovaEntityCombat.class, NovaEntityCombat::register);
    }
}
