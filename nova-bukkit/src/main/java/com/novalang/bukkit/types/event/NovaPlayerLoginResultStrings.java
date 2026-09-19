package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.player.PlayerLoginEvent;

/** Spigot 1.12.2 PlayerLoginEvent 的字符串结果别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerLoginEvent$Result"})
public final class NovaPlayerLoginResultStrings {

    private NovaPlayerLoginResultStrings() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerLoginEvent.class, "setResult", function -> function.param("result", String.class).returns(Void.TYPE).invoke(NovaPlayerLoginResultStrings::setResult));
        builder.extension(PlayerLoginEvent.class, "disallow", function -> function.param("result", String.class).param("message", String.class).returns(Void.TYPE).invoke(NovaPlayerLoginResultStrings::disallow));
    }

    private static Object setResult(Object[] arguments) {
        PlayerLoginEvent.Result result = result(arguments);
        if (result != null) {
            event(arguments).setResult(result);
        }
        return null;
    }

    private static Object disallow(Object[] arguments) {
        PlayerLoginEvent.Result result = result(arguments);
        if (result != null) {
            event(arguments).disallow(result, NovaTypeSupport.argument(arguments, 2, String.class));
        }
        return null;
    }

    private static PlayerLoginEvent.Result result(Object[] arguments) {
        return NovaTypeSupport.findEnum(PlayerLoginEvent.Result.class, NovaTypeSupport.argument(arguments, 1, String.class));
    }

    private static PlayerLoginEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerLoginEvent.class);
    }
}
