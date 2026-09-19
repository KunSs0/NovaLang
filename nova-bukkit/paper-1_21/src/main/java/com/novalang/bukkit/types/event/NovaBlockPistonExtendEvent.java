package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonExtendEvent;

/** 活塞伸出事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockPistonExtendEvent"})
@SuppressWarnings("deprecation")
public final class NovaBlockPistonExtendEvent {

    private NovaBlockPistonExtendEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockPistonExtendEvent.class, "length", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getLength()));
        builder.extension(BlockPistonExtendEvent.class, "blocks", function -> function
                .returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Block.class)))
                .invoke(arguments -> event(arguments).getBlocks()));
    }

    private static BlockPistonExtendEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockPistonExtendEvent.class);
    }
}
