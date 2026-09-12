package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPistonEvent;

/** 活塞事件基础类型的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockPistonEvent"})
public final class NovaBlockPistonEvent {

    private NovaBlockPistonEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockPistonEvent.class, "isSticky", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).isSticky()));
        builder.extension(BlockPistonEvent.class, "direction", function -> function
                .returns(BlockFace.class)
                .invoke(arguments -> event(arguments).getDirection()));
    }

    private static BlockPistonEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockPistonEvent.class);
    }
}
