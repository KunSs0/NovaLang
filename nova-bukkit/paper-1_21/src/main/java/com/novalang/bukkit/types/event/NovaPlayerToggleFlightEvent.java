package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/** 玩家切换飞行状态事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerToggleFlightEvent"})
public final class NovaPlayerToggleFlightEvent {

    private NovaPlayerToggleFlightEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerToggleFlightEvent.class, "player", function -> function
                .returns(Player.class)
                .invoke(arguments -> event(arguments).getPlayer()));
        builder.extension(PlayerToggleFlightEvent.class, "isFlying", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).isFlying()));
    }

    private static PlayerToggleFlightEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerToggleFlightEvent.class);
    }
}
