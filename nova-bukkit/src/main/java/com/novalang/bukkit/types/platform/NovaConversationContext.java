package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.plugin.Plugin;

/** ConversationContext 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.conversations.ConversationContext"})
public final class NovaConversationContext {

    private NovaConversationContext() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ConversationContext.class, "getPlugin", function -> function
                .returns(Plugin.class)
                .invoke(arguments -> context(arguments).getPlugin()));
        builder.extension(ConversationContext.class, "getForWhom", function -> function
                .returns(Conversable.class)
                .invoke(arguments -> context(arguments).getForWhom()));
        builder.extension(ConversationContext.class, "getAllSessionData", function -> function
                .returns(JavaTypeRef.mapOf(JavaTypeRef.javaType(Object.class), JavaTypeRef.javaType(Object.class)))
                .invoke(arguments -> context(arguments).getAllSessionData()));
        builder.extension(ConversationContext.class, "getSessionData", function -> function
                .param("key", Object.class)
                .returns(JavaTypeRef.javaType(Object.class).nullable())
                .invoke(arguments -> context(arguments).getSessionData(
                        NovaTypeSupport.argument(arguments, 1, Object.class))));
        builder.extension(ConversationContext.class, "setSessionData", function -> function
                .param("key", Object.class)
                .param("value", JavaTypeRef.javaType(Object.class).nullable())
                .invoke(arguments -> {
                    context(arguments).setSessionData(
                            NovaTypeSupport.argument(arguments, 1, Object.class),
                            NovaTypeSupport.argument(arguments, 2, Object.class));
                    return null;
                }));
    }

    private static ConversationContext context(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ConversationContext.class);
    }
}
