package com.novalang.bukkit.types.server;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** BanList 与 BanEntry 的补充聚合器。 */
public final class NovaServerBanTypes {
    private NovaServerBanTypes() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaBanList.class, NovaBanList::register);
        NovaBukkitRegistrar.register(builder, NovaBanEntry.class, NovaBanEntry::register);
    }
}
