package com.novalang.bukkit.types.event;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.player.PlayerPreLoginEvent;

import java.net.InetAddress;
import java.util.UUID;

/** Spigot 1.12.2 同步玩家预登录事件别名。 */
@SuppressWarnings("deprecation")
public final class NovaPlayerPreLoginEvent {

    private NovaPlayerPreLoginEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerPreLoginEvent.class, "result", function -> function
                .returns(PlayerPreLoginEvent.Result.class).invoke(arguments -> event(arguments).getResult()));
        builder.extension(PlayerPreLoginEvent.class, "setResult", function -> function
                .param("result", PlayerPreLoginEvent.Result.class).returns(Void.TYPE).invoke(arguments -> {
                    event(arguments).setResult(argument(arguments, 1, PlayerPreLoginEvent.Result.class));
                    return null;
                }));
        builder.extension(PlayerPreLoginEvent.class, "kickMessage", function -> function
                .returns(String.class).invoke(arguments -> event(arguments).getKickMessage()));
        builder.extension(PlayerPreLoginEvent.class, "setKickMessage", function -> function
                .param("message", String.class).returns(Void.TYPE).invoke(arguments -> {
                    event(arguments).setKickMessage(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(PlayerPreLoginEvent.class, "allow", function -> function.returns(Void.TYPE).invoke(arguments -> {
            event(arguments).allow();
            return null;
        }));
        builder.extension(PlayerPreLoginEvent.class, "disallow", function -> function
                .param("result", PlayerPreLoginEvent.Result.class).param("message", String.class).returns(Void.TYPE).invoke(arguments -> {
                    event(arguments).disallow(argument(arguments, 1, PlayerPreLoginEvent.Result.class), argument(arguments, 2, String.class));
                    return null;
                }));
        builder.extension(PlayerPreLoginEvent.class, "name", function -> function
                .returns(String.class).invoke(arguments -> event(arguments).getName()));
        builder.extension(PlayerPreLoginEvent.class, "address", function -> function
                .returns(InetAddress.class).invoke(arguments -> event(arguments).getAddress()));
        builder.extension(PlayerPreLoginEvent.class, "uniqueId", function -> function
                .returns(JavaTypeRef.javaType(UUID.class).nullable()).invoke(arguments -> event(arguments).getUniqueId()));
    }

    private static PlayerPreLoginEvent event(Object[] arguments) {
        return argument(arguments, 0, PlayerPreLoginEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
