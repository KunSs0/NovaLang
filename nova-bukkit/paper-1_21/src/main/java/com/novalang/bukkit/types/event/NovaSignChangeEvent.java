package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

/** 告示牌编辑事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.block.SignChangeEvent"})
public final class NovaSignChangeEvent {

    private NovaSignChangeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(SignChangeEvent.class, "player", function -> function
                .returns(Player.class)
                .invoke(arguments -> event(arguments).getPlayer()));
        builder.extension(SignChangeEvent.class, "lines", function -> function
                .returns(String[].class)
                .invoke(arguments -> event(arguments).getLines()));
        builder.extension(SignChangeEvent.class, "getLine", function -> function
                .param("index", Integer.class)
                .returns(String.class)
                .invoke(arguments -> event(arguments).getLine(argument(arguments, 1, Integer.class))));
        builder.extension(SignChangeEvent.class, "setLine", function -> function
                .param("index", Integer.class)
                .param("line", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setLine(argument(arguments, 1, Integer.class), argument(arguments, 2, String.class));
                    return null;
                }));
    }

    private static SignChangeEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, SignChangeEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
