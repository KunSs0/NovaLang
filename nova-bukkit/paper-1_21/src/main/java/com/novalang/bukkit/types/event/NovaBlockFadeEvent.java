package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockFadeEvent;

/** 方块褪变事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockFadeEvent"})
public final class NovaBlockFadeEvent {

    private NovaBlockFadeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockFadeEvent.class, "newState", function -> function
                .returns(BlockState.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, BlockFadeEvent.class).getNewState()));
    }
}
