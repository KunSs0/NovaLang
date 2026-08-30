package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

import java.net.InetAddress;
import java.util.Iterator;

/** 服务端列表 Ping 事件的可选 Fluxon 别名。 */
@Requires(classes = {
        "org.bukkit.event.server.ServerListPingEvent",
        "org.bukkit.util.CachedServerIcon"
})
public final class NovaServerListPingEvent {

    private NovaServerListPingEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ServerListPingEvent.class, "address", function -> function
                .returns(InetAddress.class)
                .invoke(arguments -> event(arguments).getAddress()));
        builder.extension(ServerListPingEvent.class, "motd", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getMotd()));
        builder.extension(ServerListPingEvent.class, "setMotd", function -> function
                .param("motd", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setMotd(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(ServerListPingEvent.class, "numPlayers", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getNumPlayers()));
        builder.extension(ServerListPingEvent.class, "maxPlayers", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getMaxPlayers()));
        builder.extension(ServerListPingEvent.class, "setMaxPlayers", function -> function
                .param("maxPlayers", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setMaxPlayers(argument(arguments, 1, Integer.class));
                    return null;
                }));
        builder.extension(ServerListPingEvent.class, "setServerIcon", function -> function
                .param("icon", JavaTypeRef.javaType(CachedServerIcon.class).nullable())
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setServerIcon(argument(arguments, 1, CachedServerIcon.class));
                    return null;
                }));
        builder.extension(ServerListPingEvent.class, "iterator", function -> function
                .returns(JavaTypeRef.javaType(Iterator.class))
                .invoke(arguments -> event(arguments).iterator()));
    }

    private static ServerListPingEvent event(Object[] arguments) {
        return argument(arguments, 0, ServerListPingEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
