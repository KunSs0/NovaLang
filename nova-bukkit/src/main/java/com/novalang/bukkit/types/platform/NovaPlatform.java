package com.novalang.bukkit.types.platform;

import com.novalang.runtime.host.JavaTypes;

/** 非 Server Bukkit 平台别名聚合入口。由宿主在组装 JavaTypes 时调用。 */
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
        NovaBossBar.register(builder);
        NovaConfiguration.register(builder);
    }
}
