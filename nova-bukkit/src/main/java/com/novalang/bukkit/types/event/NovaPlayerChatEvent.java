package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

/** 同步玩家聊天事件的可选 Spigot 1.12.2 类型别名。 */
@SuppressWarnings("deprecation")
@Requires(classes = {"org.bukkit.event.player.PlayerChatEvent"})
public final class NovaPlayerChatEvent {
    private NovaPlayerChatEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerChatEvent.class, "message", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getMessage()));
        builder.extension(PlayerChatEvent.class, "setMessage", function -> function
                .param("message", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setMessage(NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(PlayerChatEvent.class, "format", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getFormat()));
        builder.extension(PlayerChatEvent.class, "setFormat", function -> function
                .param("format", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setFormat(NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(PlayerChatEvent.class, "recipients", function -> function
                .returns(JavaTypeRef.setOf(JavaTypeRef.javaType(Player.class)))
                .invoke(arguments -> event(arguments).getRecipients()));
        builder.extension(PlayerChatEvent.class, "setPlayer", function -> function
                .param("player", Player.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setPlayer(NovaTypeSupport.argument(arguments, 1, Player.class));
                    return null;
                }));
    }

    private static PlayerChatEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerChatEvent.class);
    }
}
