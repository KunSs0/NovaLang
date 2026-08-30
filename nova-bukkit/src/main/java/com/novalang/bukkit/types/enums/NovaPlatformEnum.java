package com.novalang.bukkit.types.enums;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.BanList;
import org.bukkit.Statistic;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.plugin.PluginAwareness;
import org.bukkit.plugin.messaging.PluginChannelDirection;

/** 平台与统计体系的 Spigot 1.12.2 Fluxon 枚举入口。 */
final class NovaPlatformEnum {

    private NovaPlatformEnum() {
    }

    static void register(JavaTypes.Builder builder) {
        NovaEnum.registerEnum(builder, "banListType", BanList.Type.class);
        NovaEnum.registerEnum(builder, "barColor", BarColor.class);
        NovaEnum.registerEnum(builder, "barFlag", BarFlag.class);
        NovaEnum.registerEnum(builder, "barStyle", BarStyle.class);
        NovaEnum.registerEnum(builder, "pluginAwarenessFlags", PluginAwareness.Flags.class);
        NovaEnum.registerEnum(builder, "pluginChannelDirection", PluginChannelDirection.class);
        NovaEnum.registerEnum(builder, "statistic", Statistic.class);
        NovaEnum.registerEnum(builder, "statisticType", Statistic.Type.class);
    }
}
