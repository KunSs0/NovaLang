package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypes;

/** 第二批 Bukkit 平台类型的独立聚合器，不改 NovaBukkit 总入口。 */
final class NovaServerPlatform {

    private NovaServerPlatform() {
    }

    static void register(JavaTypes.Builder builder) {
        NovaServerExtra.register(builder);
        NovaOfflinePlayer.register(builder);
        NovaPermission.register(builder);
        NovaMetadata.register(builder);
        NovaHelp.register(builder);
        NovaServices.register(builder);
    }
}
