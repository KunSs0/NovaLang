package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

/** 玩家物品耐久损耗事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerItemDamageEvent"})
public final class NovaPlayerItemDamageEvent {

    private NovaPlayerItemDamageEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerItemDamageEvent.class, "item", function -> function
                .returns(ItemStack.class)
                .invoke(arguments -> event(arguments).getItem()));
        builder.extension(PlayerItemDamageEvent.class, "damage", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getDamage()));
        builder.extension(PlayerItemDamageEvent.class, "setDamage", function -> function
                .param("damage", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setDamage(argument(arguments, 1, Integer.class));
                    return null;
                }));
    }

    private static PlayerItemDamageEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerItemDamageEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
