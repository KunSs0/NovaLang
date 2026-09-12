package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

/** 玩家速度变更事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerVelocityEvent"})
public final class NovaPlayerVelocityEvent {

    private NovaPlayerVelocityEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerVelocityEvent.class, "player", function -> function
                .returns(Player.class)
                .invoke(arguments -> event(arguments).getPlayer()));
        builder.extension(PlayerVelocityEvent.class, "velocity", function -> function
                .returns(Vector.class)
                .invoke(arguments -> event(arguments).getVelocity()));
        builder.extension(PlayerVelocityEvent.class, "setVelocity", function -> function
                .param("velocity", Vector.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setVelocity(NovaTypeSupport.argument(arguments, 1, Vector.class));
                    return null;
                }));
    }

    private static PlayerVelocityEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerVelocityEvent.class);
    }
}
