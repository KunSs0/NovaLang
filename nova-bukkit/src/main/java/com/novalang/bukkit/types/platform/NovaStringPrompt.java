package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.StringPrompt;

/** StringPrompt 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.conversations.StringPrompt"})
public final class NovaStringPrompt {

    private NovaStringPrompt() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(StringPrompt.class, "blocksForInput", function -> function
                .param("context", ConversationContext.class)
                .returns(Boolean.class)
                .invoke(arguments -> stringPrompt(arguments).blocksForInput(
                        NovaTypeSupport.argument(arguments, 1, ConversationContext.class))));
    }

    private static StringPrompt stringPrompt(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, StringPrompt.class);
    }
}
