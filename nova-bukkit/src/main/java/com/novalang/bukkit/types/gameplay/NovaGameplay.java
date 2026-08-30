package com.novalang.bukkit.types.gameplay;

import com.novalang.bukkit.types.entity.NovaEntityCombat;
import com.novalang.bukkit.types.entity.NovaEntityCommon;
import com.novalang.bukkit.types.entity.NovaEntityObjects;
import com.novalang.bukkit.types.event.NovaBlockEvent;
import com.novalang.bukkit.types.event.NovaEntityEvent;
import com.novalang.bukkit.types.event.NovaInventoryEvent;
import com.novalang.bukkit.types.event.NovaPlayerEvent;
import com.novalang.runtime.host.JavaTypes;

/** 第二轮游戏对象注册器聚合；由 Bukkit 总入口按需调用。 */
public final class NovaGameplay {
    private NovaGameplay() { }

    public static void register(JavaTypes.Builder builder) {
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
