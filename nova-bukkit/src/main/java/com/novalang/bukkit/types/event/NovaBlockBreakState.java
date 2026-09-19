package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.block.BlockBreakEvent;

/** Spigot 1.12.2 BlockBreakEvent 掉落状态别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockBreakEvent"})
public final class NovaBlockBreakState {

    private NovaBlockBreakState() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockBreakEvent.class, "isDropItems", function -> function.returns(Boolean.class).invoke(arguments -> event(arguments).isDropItems()));
    }

    private static BlockBreakEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockBreakEvent.class);
    }
}
