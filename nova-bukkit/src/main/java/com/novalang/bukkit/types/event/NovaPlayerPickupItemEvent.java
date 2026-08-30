package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerPickupItemEvent;

/** 玩家拾取物品事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerPickupItemEvent"})
@SuppressWarnings("deprecation")
public final class NovaPlayerPickupItemEvent {

    private NovaPlayerPickupItemEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerPickupItemEvent.class, "item", function -> function
                .returns(Item.class)
                .invoke(arguments -> event(arguments).getItem()));
        builder.extension(PlayerPickupItemEvent.class, "remaining", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getRemaining()));
    }

    private static PlayerPickupItemEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerPickupItemEvent.class);
    }
}
