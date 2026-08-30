package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Bukkit;
import org.bukkit.Server;

/** Bukkit Server 全局入口。 */
final class BukkitServerJavaTypes {

    private BukkitServerJavaTypes() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.globalFunction("bukkit", function -> function
                .returns(Server.class)
                .invoke0(Server.class, Bukkit::getServer));
        builder.globalFunction("server", function -> function
                .returns(Server.class)
                .invoke0(Server.class, Bukkit::getServer));
        builder.globalFunction("broadcast", function -> function
                .param("message", String.class)
                .returns(Integer.class)
                .invoke1(String.class, Bukkit::broadcastMessage));
    }
}
