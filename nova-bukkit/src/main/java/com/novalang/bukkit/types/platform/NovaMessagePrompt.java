package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

/** MessagePrompt 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.conversations.MessagePrompt"})
public final class NovaMessagePrompt {

    private NovaMessagePrompt() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(MessagePrompt.class, "blocksForInput", function -> function
                .param("context", ConversationContext.class)
                .returns(Boolean.class)
                .invoke(arguments -> messagePrompt(arguments).blocksForInput(
                        NovaTypeSupport.argument(arguments, 1, ConversationContext.class))));
        builder.extension(MessagePrompt.class, "acceptInput", function -> function
                .param("context", ConversationContext.class)
                .param("input", JavaTypeRef.javaType(String.class).nullable())
                .returns(JavaTypeRef.javaType(Prompt.class).nullable())
                .invoke(arguments -> messagePrompt(arguments).acceptInput(
                        NovaTypeSupport.argument(arguments, 1, ConversationContext.class),
                        NovaTypeSupport.argument(arguments, 2, String.class))));
    }

    private static MessagePrompt messagePrompt(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, MessagePrompt.class);
    }
}
