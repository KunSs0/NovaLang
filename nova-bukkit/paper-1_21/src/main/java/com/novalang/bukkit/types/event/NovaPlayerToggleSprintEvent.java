package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSprintEvent;

/** 玩家切换疾跑状态事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerToggleSprintEvent"})
public final class NovaPlayerToggleSprintEvent {

    private NovaPlayerToggleSprintEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerToggleSprintEvent.class, "player", function -> function
                .returns(Player.class)
                .invoke(arguments -> event(arguments).getPlayer()));
        builder.extension(PlayerToggleSprintEvent.class, "isSprinting", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).isSprinting()));
    }

    private static PlayerToggleSprintEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerToggleSprintEvent.class);
    }
}
