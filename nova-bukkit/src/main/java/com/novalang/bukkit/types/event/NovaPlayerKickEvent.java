package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;

/** 玩家被踢出事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerKickEvent"})
public final class NovaPlayerKickEvent {

    private NovaPlayerKickEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerKickEvent.class, "player", function -> function
                .returns(Player.class)
                .invoke(arguments -> event(arguments).getPlayer()));
        builder.extension(PlayerKickEvent.class, "reason", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getReason()));
        builder.extension(PlayerKickEvent.class, "setReason", function -> function
                .param("reason", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setReason(NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(PlayerKickEvent.class, "leaveMessage", function -> function
                .returns(String.class)
                .invoke(arguments -> event(arguments).getLeaveMessage()));
        builder.extension(PlayerKickEvent.class, "setLeaveMessage", function -> function
                .param("message", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setLeaveMessage(NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
    }

    private static PlayerKickEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerKickEvent.class);
    }
}
