package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerDropItemEvent;

/** 玩家丢弃物品事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerDropItemEvent"})
public final class NovaPlayerDropItemEvent {

    private NovaPlayerDropItemEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerDropItemEvent.class, "itemDrop", function -> function
                .returns(Item.class)
                .invoke(arguments -> event(arguments).getItemDrop()));
    }

    private static PlayerDropItemEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerDropItemEvent.class);
    }
}
