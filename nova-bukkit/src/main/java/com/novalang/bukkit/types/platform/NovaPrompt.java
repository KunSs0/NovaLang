package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;

/** Prompt 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.conversations.Prompt"})
public final class NovaPrompt {

    private NovaPrompt() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Prompt.class, "getPromptText", function -> function
                .param("context", ConversationContext.class)
                .returns(String.class)
                .invoke(arguments -> prompt(arguments).getPromptText(
                        NovaTypeSupport.argument(arguments, 1, ConversationContext.class))));
        builder.extension(Prompt.class, "blocksForInput", function -> function
                .param("context", ConversationContext.class)
                .returns(Boolean.class)
                .invoke(arguments -> prompt(arguments).blocksForInput(
                        NovaTypeSupport.argument(arguments, 1, ConversationContext.class))));
        builder.extension(Prompt.class, "acceptInput", function -> function
                .param("context", ConversationContext.class)
                .param("input", JavaTypeRef.javaType(String.class).nullable())
                .returns(JavaTypeRef.javaType(Prompt.class).nullable())
                .invoke(arguments -> prompt(arguments).acceptInput(
                        NovaTypeSupport.argument(arguments, 1, ConversationContext.class),
                        NovaTypeSupport.argument(arguments, 2, String.class))));
    }

    private static Prompt prompt(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Prompt.class);
    }
}
