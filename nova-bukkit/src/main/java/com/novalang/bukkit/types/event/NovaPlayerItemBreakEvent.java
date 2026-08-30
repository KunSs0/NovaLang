package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

/** 玩家物品损坏事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerItemBreakEvent"})
public final class NovaPlayerItemBreakEvent {

    private NovaPlayerItemBreakEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerItemBreakEvent.class, "brokenItem", function -> function
                .returns(ItemStack.class)
                .invoke(arguments -> event(arguments).getBrokenItem()));
    }

    private static PlayerItemBreakEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerItemBreakEvent.class);
    }
}
