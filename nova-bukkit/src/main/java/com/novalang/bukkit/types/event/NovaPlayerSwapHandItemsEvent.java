package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

/** 玩家交换主副手事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerSwapHandItemsEvent"})
public final class NovaPlayerSwapHandItemsEvent {

    private NovaPlayerSwapHandItemsEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableItem = JavaTypeRef.javaType(ItemStack.class).nullable();
        builder.extension(PlayerSwapHandItemsEvent.class, "mainHandItem", function -> function
                .returns(nullableItem)
                .invoke(arguments -> event(arguments).getMainHandItem()));
        builder.extension(PlayerSwapHandItemsEvent.class, "setMainHandItem", function -> function
                .param("item", nullableItem)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setMainHandItem(argument(arguments, 1, ItemStack.class));
                    return null;
                }));
        builder.extension(PlayerSwapHandItemsEvent.class, "offHandItem", function -> function
                .returns(nullableItem)
                .invoke(arguments -> event(arguments).getOffHandItem()));
        builder.extension(PlayerSwapHandItemsEvent.class, "setOffHandItem", function -> function
                .param("item", nullableItem)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setOffHandItem(argument(arguments, 1, ItemStack.class));
                    return null;
                }));
    }

    private static PlayerSwapHandItemsEvent event(Object[] arguments) {
        return argument(arguments, 0, PlayerSwapHandItemsEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
