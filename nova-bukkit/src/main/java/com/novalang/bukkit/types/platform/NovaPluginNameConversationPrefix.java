package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.PluginNameConversationPrefix;

/** PluginNameConversationPrefix 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.conversations.PluginNameConversationPrefix"})
public final class NovaPluginNameConversationPrefix {

    private NovaPluginNameConversationPrefix() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PluginNameConversationPrefix.class, "getPrefix", function -> function
                .param("context", ConversationContext.class)
                .returns(String.class)
                .invoke(arguments -> prefix(arguments).getPrefix(
                        NovaTypeSupport.argument(arguments, 1, ConversationContext.class))));
    }

    private static PluginNameConversationPrefix prefix(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PluginNameConversationPrefix.class);
    }
}
