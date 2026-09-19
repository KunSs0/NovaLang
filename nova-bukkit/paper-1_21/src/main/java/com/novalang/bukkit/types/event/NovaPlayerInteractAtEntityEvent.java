package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.util.Vector;

/** 玩家精确右键实体事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerInteractAtEntityEvent"})
public final class NovaPlayerInteractAtEntityEvent {

    private NovaPlayerInteractAtEntityEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerInteractAtEntityEvent.class, "clickedPosition", function -> function
                .returns(Vector.class)
                .invoke(arguments -> event(arguments).getClickedPosition()));
    }

    private static PlayerInteractAtEntityEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerInteractAtEntityEvent.class);
    }
}
