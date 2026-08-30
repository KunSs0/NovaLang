package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

/** 玩家游戏模式变更事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerGameModeChangeEvent"})
public final class NovaPlayerGameModeChangeEvent {

    private NovaPlayerGameModeChangeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerGameModeChangeEvent.class, "player", function -> function
                .returns(Player.class)
                .invoke(arguments -> event(arguments).getPlayer()));
        builder.extension(PlayerGameModeChangeEvent.class, "newGameMode", function -> function
                .returns(GameMode.class)
                .invoke(arguments -> event(arguments).getNewGameMode()));
    }

    private static PlayerGameModeChangeEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerGameModeChangeEvent.class);
    }
}
