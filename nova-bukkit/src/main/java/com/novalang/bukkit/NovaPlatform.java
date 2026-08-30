package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypes;

/** 非 Server Bukkit 平台别名聚合入口。由宿主在组装 JavaTypes 时调用。 */
final class NovaPlatform {

    private NovaPlatform() {
    }

    static void register(JavaTypes.Builder builder) {
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
