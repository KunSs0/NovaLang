package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

/** ValidatingPrompt 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.conversations.ValidatingPrompt"})
public final class NovaValidatingPrompt {

    private NovaValidatingPrompt() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ValidatingPrompt.class, "acceptInput", function -> function
                .param("context", ConversationContext.class)
                .param("input", String.class)
                .returns(JavaTypeRef.javaType(Prompt.class).nullable())
                .invoke(arguments -> validatingPrompt(arguments).acceptInput(
                        NovaTypeSupport.argument(arguments, 1, ConversationContext.class),
                        NovaTypeSupport.argument(arguments, 2, String.class))));
        builder.extension(ValidatingPrompt.class, "blocksForInput", function -> function
                .param("context", ConversationContext.class)
                .returns(Boolean.class)
                .invoke(arguments -> validatingPrompt(arguments).blocksForInput(
                        NovaTypeSupport.argument(arguments, 1, ConversationContext.class))));
    }

    private static ValidatingPrompt validatingPrompt(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ValidatingPrompt.class);
    }
}
