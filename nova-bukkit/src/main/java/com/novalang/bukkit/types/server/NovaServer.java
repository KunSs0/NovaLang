package com.novalang.bukkit.types.server;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Bukkit;
import org.bukkit.Server;

/** Bukkit Server 全局入口。 */
public final class NovaServer {

    private NovaServer() {
    }

    public static void register(JavaTypes.Builder builder) {
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
