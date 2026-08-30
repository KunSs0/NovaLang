package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypes;

/** 1.12.2 Bukkit 实体层级别名聚合注册器。 */
final class NovaEntityHierarchy {

    private NovaEntityHierarchy() {
    }

    static void register(JavaTypes.Builder builder) {
        NovaEntityExtra.register(builder);
        NovaLivingEntity.register(builder);
        NovaHumanEntity.register(builder);
        NovaPlayerExtra.register(builder);
        NovaCreature.register(builder);
        NovaProjectile.register(builder);
        NovaVehicle.register(builder);
    }
}
