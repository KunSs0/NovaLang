package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

/** 方块受玩家破坏事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockDamageEvent"})
public final class NovaBlockDamageEvent {

    private NovaBlockDamageEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockDamageEvent.class, "player", function -> function
                .returns(Player.class)
                .invoke(arguments -> event(arguments).getPlayer()));
        builder.extension(BlockDamageEvent.class, "instaBreak", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).getInstaBreak()));
        builder.extension(BlockDamageEvent.class, "setInstaBreak", function -> function
                .param("instaBreak", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setInstaBreak(argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(BlockDamageEvent.class, "itemInHand", function -> function
                .returns(ItemStack.class)
                .invoke(arguments -> event(arguments).getItemInHand()));
    }

    private static BlockDamageEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockDamageEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
