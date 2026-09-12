package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockGrowEvent;

/** 方块自然生长事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockGrowEvent"})
public final class NovaBlockGrowEvent {

    private NovaBlockGrowEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockGrowEvent.class, "newState", function -> function
                .returns(BlockState.class)
                .invoke(arguments -> event(arguments).getNewState()));
    }

    private static BlockGrowEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockGrowEvent.class);
    }
}
