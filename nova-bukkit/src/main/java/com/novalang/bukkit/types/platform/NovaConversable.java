package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;

@Requires(classes = {"org.bukkit.conversations.Conversable"})
public final class NovaConversable {
    private NovaConversable() { }
    public static void register(JavaTypes.Builder b) {
        b.extension(Conversable.class, "isConversing", f -> f.returns(Boolean.class).invoke(a -> value(a).isConversing()));
        b.extension(Conversable.class, "acceptConversationInput", f -> f.param("input", String.class).returns(Void.TYPE).invoke(a -> { value(a).acceptConversationInput(arg(a, 1, String.class)); return null; }));
        b.extension(Conversable.class, "beginConversation", f -> f.param("conversation", Conversation.class).returns(Boolean.class).invoke(a -> value(a).beginConversation(arg(a, 1, Conversation.class))));
        b.extension(Conversable.class, "sendRawMessage", f -> f.param("message", String.class).returns(Void.TYPE).invoke(a -> { value(a).sendRawMessage(arg(a, 1, String.class)); return null; }));
    }
    private static Conversable value(Object[] a) { return NovaTypeSupport.argument(a, 0, Conversable.class); }
    private static <T> T arg(Object[] a, int i, Class<T> type) { return NovaTypeSupport.argument(a, i, type); }
}
