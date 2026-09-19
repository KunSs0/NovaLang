package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/** 库存转移动画事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.inventory.InventoryMoveItemEvent"})
public final class NovaInventoryMoveItemEvent {

    private NovaInventoryMoveItemEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(InventoryMoveItemEvent.class, "source", function -> function
                .returns(Inventory.class)
                .invoke(arguments -> event(arguments).getSource()));
        builder.extension(InventoryMoveItemEvent.class, "item", function -> function
                .returns(ItemStack.class)
                .invoke(arguments -> event(arguments).getItem()));
        builder.extension(InventoryMoveItemEvent.class, "setItem", function -> function
                .param("item", ItemStack.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setItem(argument(arguments, 1, ItemStack.class));
                    return null;
                }));
        builder.extension(InventoryMoveItemEvent.class, "destination", function -> function
                .returns(Inventory.class)
                .invoke(arguments -> event(arguments).getDestination()));
        builder.extension(InventoryMoveItemEvent.class, "initiator", function -> function
                .returns(Inventory.class)
                .invoke(arguments -> event(arguments).getInitiator()));
    }

    private static InventoryMoveItemEvent event(Object[] arguments) {
        return argument(arguments, 0, InventoryMoveItemEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
