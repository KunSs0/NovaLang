package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerBedEnterEvent;

/** 玩家进入床事件的 Spigot 1.12.2 别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerBedEnterEvent"})
public final class NovaPlayerBedEnterEvent {
    private NovaPlayerBedEnterEvent() { }
    public static void register(JavaTypes.Builder b) {
        b.extension(PlayerBedEnterEvent.class, "bed", f -> f.returns(Block.class).invoke(a -> event(a).getBed()));
    }
    private static PlayerBedEnterEvent event(Object[] a) { return NovaTypeSupport.argument(a, 0, PlayerBedEnterEvent.class); }
}
