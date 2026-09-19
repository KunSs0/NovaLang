package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockExplodeEvent;

/** 方块爆炸事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockExplodeEvent"})
public final class NovaBlockExplodeEvent {

    private NovaBlockExplodeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockExplodeEvent.class, "blockList", function -> function
                .returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Block.class)))
                .invoke(arguments -> event(arguments).blockList()));
        builder.extension(BlockExplodeEvent.class, "yield", function -> function
                .returns(Float.class)
                .invoke(arguments -> event(arguments).getYield()));
        builder.extension(BlockExplodeEvent.class, "setYield", function -> function
                .param("yield", Float.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setYield(argument(arguments, 1, Float.class));
                    return null;
                }));
    }

    private static BlockExplodeEvent event(Object[] arguments) {
        return argument(arguments, 0, BlockExplodeEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
