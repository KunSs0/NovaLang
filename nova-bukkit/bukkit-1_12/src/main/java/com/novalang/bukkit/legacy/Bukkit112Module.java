package com.novalang.bukkit.legacy;

import com.novalang.bukkit.core.BukkitModule;
import com.novalang.bukkit.core.BukkitJavaTypesModule;
import com.novalang.runtime.host.JavaTypes;
import com.novalang.bukkit.types.entity.NovaVillager;
import com.novalang.bukkit.types.entity.NovaEntityObjects;
import com.novalang.bukkit.types.event.NovaEntityPortalEvent;
import com.novalang.bukkit.types.event.NovaPlayerPortalEvent;
import com.novalang.bukkit.types.event.NovaPlayerAchievementAwardedEvent;
import com.novalang.bukkit.types.world.NovaFlowerPot;
import com.novalang.bukkit.types.world.NovaNoteBlock;
import com.novalang.bukkit.types.value.NovaBukkit112Sound;
import com.novalang.bukkit.types.world.Bukkit112LegacyWorldTypes;
import com.novalang.bukkit.types.enums.Bukkit112LegacyEnumTypes;
import com.novalang.bukkit.types.gameplay.NovaAttribute;
import com.novalang.bukkit.types.gameplay.NovaEnchantment;
import com.novalang.bukkit.types.inventory.NovaBannerMetaMoreTypes;
import com.novalang.bukkit.types.server.NovaServerExtra;
import com.novalang.bukkit.types.gameplay.NovaPotion;
import com.novalang.bukkit.types.gameplay.NovaEffect;

/** Bukkit 1.12.x 兼容模块描述。 */
public final class Bukkit112Module implements BukkitJavaTypesModule {

    /** 创建 Bukkit 1.12.x 模块描述。 */
    public Bukkit112Module() {
    }

    /** 返回 Bukkit 1.12.x 的稳定模块标识。 */
    @Override
    public String id() {
        return "bukkit-1.12";
    }

    /** 注册 Bukkit 1.12 专属 JavaTypes 扩展。 */
    @Override
    public void register(JavaTypes.Builder builder) {
        NovaVillager.register(builder);
        NovaEntityObjects.register(builder);
        NovaEntityPortalEvent.register(builder);
        NovaPlayerPortalEvent.register(builder);
        NovaPlayerAchievementAwardedEvent.register(builder);
        NovaFlowerPot.register(builder);
        NovaNoteBlock.register(builder);
        NovaBukkit112Sound.register(builder);
        Bukkit112LegacyWorldTypes.register(builder);
        Bukkit112LegacyEnumTypes.register(builder);
        NovaAttribute.register(builder);
        NovaEnchantment.register(builder);
        NovaBannerMetaMoreTypes.register(builder);
        NovaServerExtra.register(builder);
        NovaPotion.register(builder);
        NovaEffect.register(builder);
    }
}
