package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.hanging.HangingPlaceEvent;

/** 放置悬挂实体事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.hanging.HangingPlaceEvent"})
public final class NovaHangingPlaceEvent {

    private NovaHangingPlaceEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(HangingPlaceEvent.class, "player", function -> function
                .returns(Player.class)
                .invoke(arguments -> event(arguments).getPlayer()));
        builder.extension(HangingPlaceEvent.class, "block", function -> function
                .returns(Block.class)
                .invoke(arguments -> event(arguments).getBlock()));
        builder.extension(HangingPlaceEvent.class, "blockFace", function -> function
                .returns(BlockFace.class)
                .invoke(arguments -> event(arguments).getBlockFace()));
    }

    private static HangingPlaceEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, HangingPlaceEvent.class);
    }
}
