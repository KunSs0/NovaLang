package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;

/** ConversationAbandonedEvent 的 Fluxon 可调用成员。 */
@Requires(classes = {"org.bukkit.conversations.ConversationAbandonedEvent"})
public final class NovaConversationAbandonedEvent {

    private NovaConversationAbandonedEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ConversationAbandonedEvent.class, "getCanceller", function -> function
                .returns(JavaTypeRef.javaType(ConversationCanceller.class).nullable())
                .invoke(arguments -> event(arguments).getCanceller()));
        builder.extension(ConversationAbandonedEvent.class, "getContext", function -> function
                .returns(ConversationContext.class)
                .invoke(arguments -> event(arguments).getContext()));
        builder.extension(ConversationAbandonedEvent.class, "gracefulExit", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).gracefulExit()));
    }

    private static ConversationAbandonedEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ConversationAbandonedEvent.class);
    }
}
