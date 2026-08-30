package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** 1.12.2 Bukkit 实体层级别名聚合注册器。 */
public final class NovaEntityHierarchy {

    private NovaEntityHierarchy() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaEntityExtra.register(builder);
        NovaLivingEntity.register(builder);
        NovaHumanEntity.register(builder);
        NovaPlayerExtra.register(builder);
        NovaCreature.register(builder);
        NovaEntityCommon.register(builder);
        NovaDamageable.register(builder);
        NovaProjectile.register(builder);
        NovaVehicle.register(builder);
        NovaBukkitRegistrar.register(builder, NovaCreeper.class, NovaCreeper::register);
        NovaBukkitRegistrar.register(builder, NovaHorse.class, NovaHorse::register);
        NovaBukkitRegistrar.register(builder, NovaMinecart.class, NovaMinecart::register);
    }
}
