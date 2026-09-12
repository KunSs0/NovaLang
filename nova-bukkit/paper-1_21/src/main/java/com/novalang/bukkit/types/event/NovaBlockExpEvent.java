package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.block.BlockExpEvent;

/** 方块经验掉落事件的可选 Spigot 1.12.2 类型别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockExpEvent"})
public final class NovaBlockExpEvent {

    private NovaBlockExpEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockExpEvent.class, "expToDrop", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getExpToDrop()));
        builder.extension(BlockExpEvent.class, "setExpToDrop", function -> function
                .param("exp", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setExpToDrop(NovaTypeSupport.argument(arguments, 1, Integer.class));
                    return null;
                }));
    }

    private static BlockExpEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockExpEvent.class);
    }
}
