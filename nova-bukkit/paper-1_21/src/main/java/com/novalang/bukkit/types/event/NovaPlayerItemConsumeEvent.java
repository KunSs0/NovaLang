package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

/** 玩家消耗物品事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerItemConsumeEvent"})
public final class NovaPlayerItemConsumeEvent {

    private NovaPlayerItemConsumeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerItemConsumeEvent.class, "item", function -> function
                .returns(ItemStack.class)
                .invoke(arguments -> event(arguments).getItem()));
        builder.extension(PlayerItemConsumeEvent.class, "setItem", function -> function
                .param("item", ItemStack.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setItem(argument(arguments, 1, ItemStack.class));
                    return null;
                }));
    }

    private static PlayerItemConsumeEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerItemConsumeEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
