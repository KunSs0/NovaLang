package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

@Requires(classes = {"org.bukkit.event.player.PlayerUnleashEntityEvent"})
public final class NovaPlayerUnleashEntityEvent {

    private NovaPlayerUnleashEntityEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerUnleashEntityEvent.class, "player", function -> function
                .returns(Player.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, PlayerUnleashEntityEvent.class).getPlayer()));
    }
}
