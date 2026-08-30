package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityPickupItemEvent;

/** 实体拾取物品事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.entity.EntityPickupItemEvent"})
public final class NovaEntityPickupItemEvent {

    private NovaEntityPickupItemEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityPickupItemEvent.class, "item", function -> function
                .returns(Item.class)
                .invoke(arguments -> event(arguments).getItem()));
        builder.extension(EntityPickupItemEvent.class, "remaining", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getRemaining()));
    }

    private static EntityPickupItemEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityPickupItemEvent.class);
    }
}
