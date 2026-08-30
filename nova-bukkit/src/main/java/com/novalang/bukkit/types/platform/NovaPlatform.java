package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

/** 非 Server Bukkit 平台别名聚合入口。由宿主在组装 JavaTypes 时调用。 */
@Requires(classes = {"org.bukkit.Server"})
public final class NovaPlatform {

    private NovaPlatform() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaScheduler.register(builder);
        NovaPlugin.register(builder);
        NovaCommand.register(builder);
        NovaEvent.register(builder);
        NovaMessaging.register(builder);
        NovaScoreboard.register(builder);
        NovaBukkitRegistrar.register(builder, NovaScoreboardExtra.class, NovaScoreboardExtra::register);
        NovaBukkitRegistrar.register(builder, NovaObjectiveExtra.class, NovaObjectiveExtra::register);
        NovaBukkitRegistrar.register(builder, NovaTeamOptions.class, NovaTeamOptions::register);
        NovaBukkitRegistrar.register(builder, NovaBossBar.class, NovaBossBar::register);
        NovaConfiguration.register(builder);
    }
}
