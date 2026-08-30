package com.novalang.bukkit.types.server;

import com.novalang.runtime.host.JavaTypes;

/** 第二批 Bukkit 平台类型的独立聚合器，不改 NovaBukkit 总入口。 */
public final class NovaServerPlatform {

    private NovaServerPlatform() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaServerExtra.register(builder);
        NovaOfflinePlayer.register(builder);
        NovaPermission.register(builder);
        NovaMetadata.register(builder);
        NovaHelp.register(builder);
        NovaServices.register(builder);
        NovaPluginManager.register(builder);
    }
}
