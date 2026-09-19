package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonRetractEvent;

/** 活塞收回事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockPistonRetractEvent"})
public final class NovaBlockPistonRetractEvent {

    private NovaBlockPistonRetractEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockPistonRetractEvent.class, "blocks", function -> function
                .returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Block.class)))
                .invoke(arguments -> event(arguments).getBlocks()));
    }

    private static BlockPistonRetractEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockPistonRetractEvent.class);
    }
}
