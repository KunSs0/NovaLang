package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.inventory.ItemStack;

@Requires(classes = {"org.bukkit.event.inventory.FurnaceBurnEvent"})
public final class NovaFurnaceBurnEvent {
    private NovaFurnaceBurnEvent() { }
    public static void register(JavaTypes.Builder b) {
        b.extension(FurnaceBurnEvent.class, "fuel", f -> f.returns(ItemStack.class).invoke(a -> e(a).getFuel()));
        b.extension(FurnaceBurnEvent.class, "burnTime", f -> f.returns(Integer.class).invoke(a -> e(a).getBurnTime()));
        b.extension(FurnaceBurnEvent.class, "setBurnTime", f -> f.param("time", Integer.class).returns(Void.TYPE).invoke(a -> { e(a).setBurnTime(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        b.extension(FurnaceBurnEvent.class, "isBurning", f -> f.returns(Boolean.class).invoke(a -> e(a).isBurning()));
        b.extension(FurnaceBurnEvent.class, "setBurning", f -> f.param("burning", Boolean.class).returns(Void.TYPE).invoke(a -> { e(a).setBurning(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
    }
    private static FurnaceBurnEvent e(Object[] a) { return NovaTypeSupport.argument(a, 0, FurnaceBurnEvent.class); }
}
