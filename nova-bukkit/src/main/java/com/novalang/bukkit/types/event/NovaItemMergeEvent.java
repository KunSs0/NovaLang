package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.ItemMergeEvent;

@Requires(classes = {"org.bukkit.event.entity.ItemMergeEvent"})
public final class NovaItemMergeEvent {
    private NovaItemMergeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ItemMergeEvent.class, "entity", function -> function.returns(Entity.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(ItemMergeEvent.class, "target", function -> function.returns(Item.class).invoke(arguments -> event(arguments).getTarget()));
    }

    private static ItemMergeEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ItemMergeEvent.class);
    }
}
