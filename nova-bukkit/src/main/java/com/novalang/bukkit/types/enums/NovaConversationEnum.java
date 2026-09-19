package com.novalang.bukkit.types.enums;
import com.novalang.bukkit.Requires; import com.novalang.runtime.host.JavaTypes; import org.bukkit.conversations.Conversation;
@Requires(classes = {"org.bukkit.conversations.Conversation$ConversationState"}) public final class NovaConversationEnum { private NovaConversationEnum() { } public static void register(JavaTypes.Builder b) { NovaEnum.registerEnum(b,"conversationState",Conversation.ConversationState.class); } }
