package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLevelChangeEvent;

/** 玩家等级变更事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerLevelChangeEvent"})
public final class NovaPlayerLevelChangeEvent {

    private NovaPlayerLevelChangeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerLevelChangeEvent.class, "player", function -> function
                .returns(Player.class)
                .invoke(arguments -> event(arguments).getPlayer()));
        builder.extension(PlayerLevelChangeEvent.class, "oldLevel", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getOldLevel()));
        builder.extension(PlayerLevelChangeEvent.class, "newLevel", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getNewLevel()));
    }

    private static PlayerLevelChangeEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerLevelChangeEvent.class);
    }
}
