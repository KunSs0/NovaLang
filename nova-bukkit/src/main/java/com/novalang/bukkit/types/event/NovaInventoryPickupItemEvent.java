package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Item;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

@Requires(classes = {"org.bukkit.event.inventory.InventoryPickupItemEvent"})
public final class NovaInventoryPickupItemEvent {
    private NovaInventoryPickupItemEvent() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(InventoryPickupItemEvent.class, "item", f -> f.returns(Item.class).invoke(a -> ((InventoryPickupItemEvent) a[0]).getItem()));
    }
}
