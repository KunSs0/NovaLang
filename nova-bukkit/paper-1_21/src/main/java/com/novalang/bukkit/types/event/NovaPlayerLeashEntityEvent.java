package com.novalang.bukkit.types.event;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerLeashEntityEvent;

/** Spigot 1.12.2 玩家拴绳实体事件别名。 */
public final class NovaPlayerLeashEntityEvent {

    private NovaPlayerLeashEntityEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerLeashEntityEvent.class, "leashHolder", function -> function
                .returns(Entity.class).invoke(arguments -> event(arguments).getLeashHolder()));
        builder.extension(PlayerLeashEntityEvent.class, "entity", function -> function
                .returns(Entity.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(PlayerLeashEntityEvent.class, "player", function -> function
                .returns(Player.class).invoke(arguments -> event(arguments).getPlayer()));
    }

    private static PlayerLeashEntityEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerLeashEntityEvent.class);
    }
}
