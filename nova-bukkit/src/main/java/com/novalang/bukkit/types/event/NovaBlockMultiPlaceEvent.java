package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockMultiPlaceEvent;

/** 多方块放置事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockMultiPlaceEvent"})
public final class NovaBlockMultiPlaceEvent {

    private NovaBlockMultiPlaceEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockMultiPlaceEvent.class, "replacedBlockStates", function -> function
                .returns(JavaTypeRef.listOf(JavaTypeRef.javaType(BlockState.class)))
                .invoke(arguments -> event(arguments).getReplacedBlockStates()));
    }

    private static BlockMultiPlaceEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockMultiPlaceEvent.class);
    }
}
