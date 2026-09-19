package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.player.PlayerLoginEvent;

import java.net.InetAddress;

/** 玩家登录事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerLoginEvent"})
public final class NovaPlayerLoginEvent {

    private NovaPlayerLoginEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerLoginEvent.class, "result", function -> function
                .returns(PlayerLoginEvent.Result.class)
                .invoke(arguments -> event(arguments).getResult()));
        builder.extension(PlayerLoginEvent.class, "setResult", function -> function
                .param("result", PlayerLoginEvent.Result.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setResult(argument(arguments, 1, PlayerLoginEvent.Result.class));
                    return null;
                }));
        builder.extension(PlayerLoginEvent.class, "kickMessage", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getKickMessage()));
        builder.extension(PlayerLoginEvent.class, "setKickMessage", function -> function
                .param("message", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setKickMessage(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(PlayerLoginEvent.class, "hostname", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getHostname()));
        builder.extension(PlayerLoginEvent.class, "allow", function -> function
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).allow();
                    return null;
                }));
        builder.extension(PlayerLoginEvent.class, "disallow", function -> function
                .param("result", PlayerLoginEvent.Result.class)
                .param("message", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).disallow(
                            argument(arguments, 1, PlayerLoginEvent.Result.class),
                            argument(arguments, 2, String.class));
                    return null;
                }));
        builder.extension(PlayerLoginEvent.class, "address", function -> function
                .returns(JavaTypeRef.javaType(InetAddress.class).nullable())
                .invoke(arguments -> event(arguments).getAddress()));
        builder.extension(PlayerLoginEvent.class, "realAddress", function -> function
                .returns(InetAddress.class)
                .invoke(arguments -> event(arguments).getRealAddress()));
    }

    private static PlayerLoginEvent event(Object[] arguments) {
        return argument(arguments, 0, PlayerLoginEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
