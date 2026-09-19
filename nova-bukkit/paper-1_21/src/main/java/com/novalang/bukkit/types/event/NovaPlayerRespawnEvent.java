package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerRespawnEvent;

/** 玩家重生事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerRespawnEvent"})
public final class NovaPlayerRespawnEvent {

    private NovaPlayerRespawnEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerRespawnEvent.class, "respawnLocation", function -> function
                .returns(Location.class)
                .invoke(arguments -> event(arguments).getRespawnLocation()));
        builder.extension(PlayerRespawnEvent.class, "setRespawnLocation", function -> function
                .param("location", Location.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setRespawnLocation(argument(arguments, 1, Location.class));
                    return null;
                }));
        builder.extension(PlayerRespawnEvent.class, "bedSpawn", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).isBedSpawn()));
        builder.extension(PlayerRespawnEvent.class, "isBedSpawn", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).isBedSpawn()));
    }

    private static PlayerRespawnEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerRespawnEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
