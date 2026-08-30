package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/** 玩家切换潜行状态事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerToggleSneakEvent"})
public final class NovaPlayerToggleSneakEvent {

    private NovaPlayerToggleSneakEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerToggleSneakEvent.class, "player", function -> function
                .returns(Player.class)
                .invoke(arguments -> event(arguments).getPlayer()));
        builder.extension(PlayerToggleSneakEvent.class, "isSneaking", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).isSneaking()));
    }

    private static PlayerToggleSneakEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerToggleSneakEvent.class);
    }
}
