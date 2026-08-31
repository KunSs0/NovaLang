package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;

/** ConversationCanceller 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.conversations.ConversationCanceller"})
public final class NovaConversationCanceller {

    private NovaConversationCanceller() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ConversationCanceller.class, "setConversation", function -> function
                .param("conversation", Conversation.class)
                .invoke(arguments -> {
                    canceller(arguments).setConversation(
                            NovaTypeSupport.argument(arguments, 1, Conversation.class));
                    return null;
                }));
        builder.extension(ConversationCanceller.class, "cancelBasedOnInput", function -> function
                .param("context", ConversationContext.class)
                .param("input", String.class)
                .returns(Boolean.class)
                .invoke(arguments -> canceller(arguments).cancelBasedOnInput(
                        NovaTypeSupport.argument(arguments, 1, ConversationContext.class),
                        NovaTypeSupport.argument(arguments, 2, String.class))));
        builder.extension(ConversationCanceller.class, "clone", function -> function
                .returns(ConversationCanceller.class)
                .invoke(arguments -> canceller(arguments).clone()));
    }

    private static ConversationCanceller canceller(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ConversationCanceller.class);
    }
}
