package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

import java.net.InetAddress;
import java.util.UUID;

/** 异步玩家预登录事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.player.AsyncPlayerPreLoginEvent"})
@SuppressWarnings("deprecation")
public final class NovaAsyncPlayerPreLoginEvent {

    private NovaAsyncPlayerPreLoginEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(AsyncPlayerPreLoginEvent.class, "loginResult", function -> function
                .returns(AsyncPlayerPreLoginEvent.Result.class)
                .invoke(arguments -> event(arguments).getLoginResult()));
        builder.extension(AsyncPlayerPreLoginEvent.class, "setLoginResult", function -> function
                .param("result", AsyncPlayerPreLoginEvent.Result.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setLoginResult(argument(arguments, 1, AsyncPlayerPreLoginEvent.Result.class));
                    return null;
                }));
        builder.extension(AsyncPlayerPreLoginEvent.class, "result", function -> function
                .returns(PlayerPreLoginEvent.Result.class)
                .invoke(arguments -> event(arguments).getResult()));
        builder.extension(AsyncPlayerPreLoginEvent.class, "setResult", function -> function
                .param("result", PlayerPreLoginEvent.Result.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setResult(argument(arguments, 1, PlayerPreLoginEvent.Result.class));
                    return null;
                }));
        builder.extension(AsyncPlayerPreLoginEvent.class, "kickMessage", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getKickMessage()));
        builder.extension(AsyncPlayerPreLoginEvent.class, "setKickMessage", function -> function
                .param("message", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setKickMessage(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(AsyncPlayerPreLoginEvent.class, "allow", function -> function
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).allow();
                    return null;
                }));
        builder.extension(AsyncPlayerPreLoginEvent.class, "disallow", function -> function
                .param("result", AsyncPlayerPreLoginEvent.Result.class)
                .param("message", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).disallow(
                            argument(arguments, 1, AsyncPlayerPreLoginEvent.Result.class),
                            argument(arguments, 2, String.class));
                    return null;
                }));
        builder.extension(AsyncPlayerPreLoginEvent.class, "disallow", function -> function
                .param("result", PlayerPreLoginEvent.Result.class)
                .param("message", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).disallow(
                            argument(arguments, 1, PlayerPreLoginEvent.Result.class),
                            argument(arguments, 2, String.class));
                    return null;
                }));
        builder.extension(AsyncPlayerPreLoginEvent.class, "name", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getName()));
        builder.extension(AsyncPlayerPreLoginEvent.class, "address", function -> function
                .returns(InetAddress.class)
                .invoke(arguments -> event(arguments).getAddress()));
        builder.extension(AsyncPlayerPreLoginEvent.class, "uniqueId", function -> function
                .returns(UUID.class)
                .invoke(arguments -> event(arguments).getUniqueId()));
    }

    private static AsyncPlayerPreLoginEvent event(Object[] arguments) {
        return argument(arguments, 0, AsyncPlayerPreLoginEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
