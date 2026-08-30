package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/** 发射器发射事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockDispenseEvent"})
public final class NovaBlockDispenseEvent {

    private NovaBlockDispenseEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockDispenseEvent.class, "item", function -> function
                .returns(ItemStack.class)
                .invoke(arguments -> event(arguments).getItem()));
        builder.extension(BlockDispenseEvent.class, "setItem", function -> function
                .param("item", ItemStack.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setItem(argument(arguments, 1, ItemStack.class));
                    return null;
                }));
        builder.extension(BlockDispenseEvent.class, "velocity", function -> function
                .returns(Vector.class)
                .invoke(arguments -> event(arguments).getVelocity()));
        builder.extension(BlockDispenseEvent.class, "setVelocity", function -> function
                .param("velocity", Vector.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setVelocity(argument(arguments, 1, Vector.class));
                    return null;
                }));
    }

    private static BlockDispenseEvent event(Object[] arguments) {
        return argument(arguments, 0, BlockDispenseEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
