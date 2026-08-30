package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerBedLeaveEvent;

/** 玩家离床事件的 Spigot 1.12.2 别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerBedLeaveEvent"})
public final class NovaPlayerBedLeaveEvent {
    private NovaPlayerBedLeaveEvent() { }
    public static void register(JavaTypes.Builder b) {
        b.extension(PlayerBedLeaveEvent.class, "bed", f -> f.returns(Block.class).invoke(a -> event(a).getBed()));
    }
    private static PlayerBedLeaveEvent event(Object[] a) { return NovaTypeSupport.argument(a, 0, PlayerBedLeaveEvent.class); }
}
