package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.Prompt;

/** ConversationFactory 的 Fluxon 链式构造成员。 */
@Requires(classes = {"org.bukkit.conversations.ConversationFactory"})
public final class NovaConversationFactory {

    private NovaConversationFactory() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ConversationFactory.class, "withModality", function -> function
                .param("modal", Boolean.class)
                .returns(ConversationFactory.class)
                .invoke(arguments -> factory(arguments).withModality(
                        NovaTypeSupport.argument(arguments, 1, Boolean.class))));
        builder.extension(ConversationFactory.class, "withLocalEcho", function -> function
                .param("enabled", Boolean.class)
                .returns(ConversationFactory.class)
                .invoke(arguments -> factory(arguments).withLocalEcho(
                        NovaTypeSupport.argument(arguments, 1, Boolean.class))));
        builder.extension(ConversationFactory.class, "withPrefix", function -> function
                .param("prefix", ConversationPrefix.class)
                .returns(ConversationFactory.class)
                .invoke(arguments -> factory(arguments).withPrefix(
                        NovaTypeSupport.argument(arguments, 1, ConversationPrefix.class))));
        builder.extension(ConversationFactory.class, "withTimeout", function -> function
                .param("seconds", Integer.class)
                .returns(ConversationFactory.class)
                .invoke(arguments -> factory(arguments).withTimeout(
                        NovaTypeSupport.argument(arguments, 1, Integer.class))));
        builder.extension(ConversationFactory.class, "withFirstPrompt", function -> function
                .param("prompt", Prompt.class)
                .returns(ConversationFactory.class)
                .invoke(arguments -> factory(arguments).withFirstPrompt(
                        NovaTypeSupport.argument(arguments, 1, Prompt.class))));
        builder.extension(ConversationFactory.class, "withEscapeSequence", function -> function
                .param("sequence", String.class)
                .returns(ConversationFactory.class)
                .invoke(arguments -> factory(arguments).withEscapeSequence(
                        NovaTypeSupport.argument(arguments, 1, String.class))));
        builder.extension(ConversationFactory.class, "withConversationCanceller", function -> function
                .param("canceller", ConversationCanceller.class)
                .returns(ConversationFactory.class)
                .invoke(arguments -> factory(arguments).withConversationCanceller(
                        NovaTypeSupport.argument(arguments, 1, ConversationCanceller.class))));
        builder.extension(ConversationFactory.class, "thatExcludesNonPlayersWithMessage", function -> function
                .param("message", String.class)
                .returns(ConversationFactory.class)
                .invoke(arguments -> factory(arguments).thatExcludesNonPlayersWithMessage(
                        NovaTypeSupport.argument(arguments, 1, String.class))));
        builder.extension(ConversationFactory.class, "addConversationAbandonedListener", function -> function
                .param("listener", ConversationAbandonedListener.class)
                .returns(ConversationFactory.class)
                .invoke(arguments -> factory(arguments).addConversationAbandonedListener(
                        NovaTypeSupport.argument(arguments, 1, ConversationAbandonedListener.class))));
        builder.extension(ConversationFactory.class, "buildConversation", function -> function
                .param("forWhom", Conversable.class)
                .returns(Conversation.class)
                .invoke(arguments -> factory(arguments).buildConversation(
                        NovaTypeSupport.argument(arguments, 1, Conversable.class))));
    }

    private static ConversationFactory factory(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ConversationFactory.class);
    }
}
