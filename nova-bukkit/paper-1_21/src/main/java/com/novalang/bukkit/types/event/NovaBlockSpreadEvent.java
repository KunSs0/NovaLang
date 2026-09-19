package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockSpreadEvent;

/** 方块蔓延事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockSpreadEvent"})
public final class NovaBlockSpreadEvent {

    private NovaBlockSpreadEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockSpreadEvent.class, "source", function -> function
                .returns(Block.class)
                .invoke(arguments -> event(arguments).getSource()));
    }

    private static BlockSpreadEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockSpreadEvent.class);
    }
}
