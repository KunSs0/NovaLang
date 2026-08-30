package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.player.PlayerItemHeldEvent;

/** 玩家切换手持栏位事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerItemHeldEvent"})
public final class NovaPlayerItemHeldEvent {

    private NovaPlayerItemHeldEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerItemHeldEvent.class, "previousSlot", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getPreviousSlot()));
        builder.extension(PlayerItemHeldEvent.class, "newSlot", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getNewSlot()));
    }

    private static PlayerItemHeldEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerItemHeldEvent.class);
    }
}
