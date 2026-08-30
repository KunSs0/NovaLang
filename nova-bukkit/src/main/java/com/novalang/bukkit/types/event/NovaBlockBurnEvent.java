package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBurnEvent;

/** 方块燃烧事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockBurnEvent"})
public final class NovaBlockBurnEvent {

    private NovaBlockBurnEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockBurnEvent.class, "ignitingBlock", function -> function
                .returns(JavaTypeRef.javaType(Block.class).nullable())
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, BlockBurnEvent.class).getIgnitingBlock()));
    }
}
