package com.novalang.bukkit.paper;

import com.novalang.bukkit.core.BukkitModule;
import com.novalang.bukkit.core.BukkitJavaTypesModule;
import com.novalang.runtime.host.JavaTypes;
import com.novalang.bukkit.types.event.NovaPlayerAdvancementDoneEvent;
import com.novalang.bukkit.types.event.NovaPlayerChangedWorldEvent;
import com.novalang.bukkit.types.event.NovaPlayerLocaleChangeEvent;
import com.novalang.bukkit.types.event.NovaPlayerBedEnterEvent;
import com.novalang.bukkit.types.event.NovaPlayerBedLeaveEvent;
import com.novalang.bukkit.types.event.NovaPlayerChangedMainHandEvent;
import com.novalang.bukkit.types.event.NovaPlayerGameModeChangeEvent;
import com.novalang.bukkit.types.event.NovaPlayerFishEvent;
import com.novalang.bukkit.types.event.NovaPlayerResourcePackStatusEvent;
import com.novalang.bukkit.types.event.NovaPlayerEggThrowEvent;
import com.novalang.bukkit.types.event.NovaPlayerDeathEvent;
import com.novalang.bukkit.types.event.NovaPlayerEditBookEvent;
import com.novalang.bukkit.types.event.NovaPlayerItemBreakEvent;
import com.novalang.bukkit.types.event.NovaPlayerItemConsumeEvent;
import com.novalang.bukkit.types.event.NovaPlayerItemDamageEvent;
import com.novalang.bukkit.types.event.NovaPlayerItemHeldEvent;
import com.novalang.bukkit.types.event.NovaPlayerKickEvent;
import com.novalang.bukkit.types.event.NovaPlayerLeashEntityEvent;
import com.novalang.bukkit.types.event.NovaPlayerLoginEvent;
import com.novalang.bukkit.types.event.NovaPlayerRespawnEvent;
import com.novalang.bukkit.types.event.NovaPlayerShearEntityEvent;
import com.novalang.bukkit.types.event.NovaPlayerSwapHandItemsEvent;
import com.novalang.bukkit.types.event.NovaPlayerInteractEvent;
import com.novalang.bukkit.types.event.NovaPlayerInteractEntityEvent;
import com.novalang.bukkit.types.event.NovaPlayerInteractAtEntityEvent;
import com.novalang.bukkit.types.event.NovaPlayerBucketEvent;
import com.novalang.bukkit.types.event.NovaPlayerExpChangeEvent;
import com.novalang.bukkit.types.event.NovaPlayerUnleashEntityEvent;
import com.novalang.bukkit.types.event.NovaPlayerToggleFlightEvent;
import com.novalang.bukkit.types.event.NovaPlayerToggleSneakEvent;
import com.novalang.bukkit.types.event.NovaPlayerToggleSprintEvent;
import com.novalang.bukkit.types.event.NovaPlayerArmorStandManipulateEvent;
import com.novalang.bukkit.types.event.NovaPlayerVelocityEvent;
import com.novalang.bukkit.types.event.NovaPlayerDropItemEvent;
import com.novalang.bukkit.types.event.NovaPlayerPickupItemEvent;
import com.novalang.bukkit.types.event.NovaPlayerPickupArrowEvent;
import com.novalang.bukkit.types.event.NovaPlayerShearEntityEvent;
import com.novalang.bukkit.types.event.NovaPlayerSwapHandItemsEvent;

/** Paper 1.21.x 默认平台模块描述。 */
public final class Paper121Module implements BukkitJavaTypesModule {

    /** 创建 Paper 1.21.x 模块描述。 */
    public Paper121Module() {
    }

    /** 返回 Paper 1.21.x 的稳定模块标识。 */
    @Override
    public String id() {
        return "paper-1.21";
    }

    /** 注册 Paper 1.21 专属 JavaTypes 扩展。 */
    @Override
    public void register(JavaTypes.Builder builder) {
        NovaPaperPlayer.register(builder);
        NovaPlayerAdvancementDoneEvent.register(builder);
        NovaPlayerChangedWorldEvent.register(builder);
        NovaPlayerLocaleChangeEvent.register(builder);
        NovaPlayerBedEnterEvent.register(builder);
        NovaPlayerBedLeaveEvent.register(builder);
        NovaPlayerChangedMainHandEvent.register(builder);
        NovaPlayerGameModeChangeEvent.register(builder);
        NovaPlayerFishEvent.register(builder);
        NovaPlayerResourcePackStatusEvent.register(builder);
        NovaPlayerDropItemEvent.register(builder);
        NovaPlayerEggThrowEvent.register(builder);
        NovaPlayerDeathEvent.register(builder);
        NovaPlayerEditBookEvent.register(builder);
        NovaPlayerItemBreakEvent.register(builder);
        NovaPlayerItemConsumeEvent.register(builder);
        NovaPlayerItemDamageEvent.register(builder);
        NovaPlayerItemHeldEvent.register(builder);
        NovaPlayerKickEvent.register(builder);
        NovaPlayerLeashEntityEvent.register(builder);
        NovaPlayerLoginEvent.register(builder);
        NovaPlayerRespawnEvent.register(builder);
        NovaPlayerShearEntityEvent.register(builder);
        NovaPlayerSwapHandItemsEvent.register(builder);
        NovaPlayerInteractEvent.register(builder);
        NovaPlayerInteractEntityEvent.register(builder);
        NovaPlayerInteractAtEntityEvent.register(builder);
        NovaPlayerBucketEvent.register(builder);
        NovaPlayerExpChangeEvent.register(builder);
        NovaPlayerUnleashEntityEvent.register(builder);
        NovaPlayerToggleFlightEvent.register(builder);
        NovaPlayerToggleSneakEvent.register(builder);
        NovaPlayerToggleSprintEvent.register(builder);
        NovaPlayerArmorStandManipulateEvent.register(builder);
        NovaPlayerVelocityEvent.register(builder);
        NovaPlayerPickupItemEvent.register(builder);
        NovaPlayerPickupArrowEvent.register(builder);
        NovaPlayerShearEntityEvent.register(builder);
        NovaPlayerSwapHandItemsEvent.register(builder);
    }
}
