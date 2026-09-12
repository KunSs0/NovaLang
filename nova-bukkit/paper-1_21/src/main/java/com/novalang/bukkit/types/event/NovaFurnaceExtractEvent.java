package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.FurnaceExtractEvent;

@Requires(classes = {"org.bukkit.event.inventory.FurnaceExtractEvent"})
public final class NovaFurnaceExtractEvent {
    private NovaFurnaceExtractEvent() { }
    public static void register(JavaTypes.Builder b) {
        b.extension(FurnaceExtractEvent.class, "player", f -> f.returns(Player.class).invoke(a -> e(a).getPlayer()));
        b.extension(FurnaceExtractEvent.class, "itemType", f -> f.returns(Material.class).invoke(a -> e(a).getItemType()));
        b.extension(FurnaceExtractEvent.class, "itemAmount", f -> f.returns(Integer.class).invoke(a -> e(a).getItemAmount()));
    }
    private static FurnaceExtractEvent e(Object[] a) { return NovaTypeSupport.argument(a, 0, FurnaceExtractEvent.class); }
}
