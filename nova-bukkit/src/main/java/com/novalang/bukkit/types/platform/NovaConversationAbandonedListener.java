package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;

/** ConversationAbandonedListener 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.conversations.ConversationAbandonedListener"})
public final class NovaConversationAbandonedListener {

    private NovaConversationAbandonedListener() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ConversationAbandonedListener.class, "conversationAbandoned", function -> function
                .param("event", ConversationAbandonedEvent.class)
                .invoke(arguments -> {
                    listener(arguments).conversationAbandoned(
                            NovaTypeSupport.argument(arguments, 1, ConversationAbandonedEvent.class));
                    return null;
                }));
    }

    private static ConversationAbandonedListener listener(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ConversationAbandonedListener.class);
    }
}
