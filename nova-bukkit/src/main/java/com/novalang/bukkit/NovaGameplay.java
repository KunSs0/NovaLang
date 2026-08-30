package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypes;

/** 第二轮游戏对象注册器聚合；由 Bukkit 总入口按需调用。 */
final class NovaGameplay {
    private NovaGameplay() { }

    static void register(JavaTypes.Builder builder) {
        NovaAttribute.register(builder);
        NovaEnchantment.register(builder);
        NovaPotion.register(builder);
        NovaProjectileSource.register(builder);
        NovaEntityCommon.register(builder);
        NovaEntityCombat.register(builder);
        NovaEntityObjects.register(builder);
        NovaPlayerEvent.register(builder);
        NovaEntityEvent.register(builder);
        NovaBlockEvent.register(builder);
        NovaInventoryEvent.register(builder);
    }
}
