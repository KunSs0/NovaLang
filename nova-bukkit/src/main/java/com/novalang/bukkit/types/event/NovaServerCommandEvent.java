package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.command.CommandSender;
import org.bukkit.event.server.ServerCommandEvent;

/** 控制台与远程命令事件共用的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.server.ServerCommandEvent"})
public final class NovaServerCommandEvent {

    private NovaServerCommandEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ServerCommandEvent.class, "command", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getCommand()));
        builder.extension(ServerCommandEvent.class, "setCommand", function -> function
                .param("command", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setCommand(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(ServerCommandEvent.class, "sender", function -> function
                .returns(CommandSender.class)
                .invoke(arguments -> event(arguments).getSender()));
    }

    private static ServerCommandEvent event(Object[] arguments) {
        return argument(arguments, 0, ServerCommandEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
