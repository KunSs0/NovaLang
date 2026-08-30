package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

/** 异步聊天事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.AsyncPlayerChatEvent"})
public final class NovaChatEvent {

    private NovaChatEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(AsyncPlayerChatEvent.class, "message", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getMessage()));
        builder.extension(AsyncPlayerChatEvent.class, "setMessage", function -> function
                .param("message", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setMessage(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(AsyncPlayerChatEvent.class, "format", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getFormat()));
        builder.extension(AsyncPlayerChatEvent.class, "setFormat", function -> function
                .param("format", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setFormat(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(AsyncPlayerChatEvent.class, "recipients", function -> function
                .returns(Set.class)
                .invoke(arguments -> event(arguments).getRecipients()));
        builder.extension(AsyncPlayerChatEvent.class, "asynchronous", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).isAsynchronous()));
    }

    private static AsyncPlayerChatEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, AsyncPlayerChatEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
