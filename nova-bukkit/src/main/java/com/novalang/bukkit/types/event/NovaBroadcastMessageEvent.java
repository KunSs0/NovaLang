package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.command.CommandSender;
import org.bukkit.event.server.BroadcastMessageEvent;

/** 广播消息事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.server.BroadcastMessageEvent"})
public final class NovaBroadcastMessageEvent {

    private NovaBroadcastMessageEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BroadcastMessageEvent.class, "message", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getMessage()));
        builder.extension(BroadcastMessageEvent.class, "setMessage", function -> function
                .param("message", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setMessage(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(BroadcastMessageEvent.class, "recipients", function -> function
                .returns(JavaTypeRef.setOf(JavaTypeRef.javaType(CommandSender.class)))
                .invoke(arguments -> event(arguments).getRecipients()));
    }

    private static BroadcastMessageEvent event(Object[] arguments) {
        return argument(arguments, 0, BroadcastMessageEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
