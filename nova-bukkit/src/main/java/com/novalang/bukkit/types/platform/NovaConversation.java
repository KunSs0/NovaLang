package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationPrefix;

import java.util.List;

/** Conversation 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.conversations.Conversation"})
public final class NovaConversation {

    private NovaConversation() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Conversation.class, "getForWhom", function -> function
                .returns(org.bukkit.conversations.Conversable.class)
                .invoke(arguments -> conversation(arguments).getForWhom()));
        builder.extension(Conversation.class, "isModal", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> conversation(arguments).isModal()));
        builder.extension(Conversation.class, "isLocalEchoEnabled", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> conversation(arguments).isLocalEchoEnabled()));
        builder.extension(Conversation.class, "setLocalEchoEnabled", function -> function
                .param("enabled", Boolean.class)
                .invoke(arguments -> {
                    conversation(arguments).setLocalEchoEnabled(
                            NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(Conversation.class, "getPrefix", function -> function
                .returns(ConversationPrefix.class)
                .invoke(arguments -> conversation(arguments).getPrefix()));
        builder.extension(Conversation.class, "getCancellers", function -> function
                .returns(JavaTypeRef.listOf(JavaTypeRef.javaType(ConversationCanceller.class)))
                .invoke(arguments -> cancellers(arguments)));
        builder.extension(Conversation.class, "getContext", function -> function
                .returns(ConversationContext.class)
                .invoke(arguments -> conversation(arguments).getContext()));
        builder.extension(Conversation.class, "begin", function -> function
                .invoke(arguments -> {
                    conversation(arguments).begin();
                    return null;
                }));
        builder.extension(Conversation.class, "acceptInput", function -> function
                .param("input", String.class)
                .invoke(arguments -> {
                    conversation(arguments).acceptInput(NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(Conversation.class, "addConversationAbandonedListener", function -> function
                .param("listener", ConversationAbandonedListener.class)
                .invoke(arguments -> {
                    conversation(arguments).addConversationAbandonedListener(
                            NovaTypeSupport.argument(arguments, 1, ConversationAbandonedListener.class));
                    return null;
                }));
        builder.extension(Conversation.class, "removeConversationAbandonedListener", function -> function
                .param("listener", ConversationAbandonedListener.class)
                .invoke(arguments -> {
                    conversation(arguments).removeConversationAbandonedListener(
                            NovaTypeSupport.argument(arguments, 1, ConversationAbandonedListener.class));
                    return null;
                }));
        builder.extension(Conversation.class, "abandon", function -> function
                .invoke(arguments -> {
                    conversation(arguments).abandon();
                    return null;
                }));
        builder.extension(Conversation.class, "abandon", function -> function
                .param("event", ConversationAbandonedEvent.class)
                .invoke(arguments -> {
                    conversation(arguments).abandon(
                            NovaTypeSupport.argument(arguments, 1, ConversationAbandonedEvent.class));
                    return null;
                }));
        builder.extension(Conversation.class, "outputNextPrompt", function -> function
                .invoke(arguments -> {
                    conversation(arguments).outputNextPrompt();
                    return null;
                }));
    }

    private static Conversation conversation(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Conversation.class);
    }

    private static List<ConversationCanceller> cancellers(Object[] arguments) {
        return conversation(arguments).getCancellers();
    }
}
