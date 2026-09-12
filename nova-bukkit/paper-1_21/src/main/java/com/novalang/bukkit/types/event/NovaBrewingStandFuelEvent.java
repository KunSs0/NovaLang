package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.inventory.ItemStack;

@Requires(classes = {"org.bukkit.event.inventory.BrewingStandFuelEvent"})
public final class NovaBrewingStandFuelEvent {
    private NovaBrewingStandFuelEvent() { }
    public static void register(JavaTypes.Builder b) {
        b.extension(BrewingStandFuelEvent.class, "fuel", f -> f.returns(ItemStack.class).invoke(a -> e(a).getFuel()));
        b.extension(BrewingStandFuelEvent.class, "fuelPower", f -> f.returns(Integer.class).invoke(a -> e(a).getFuelPower()));
        b.extension(BrewingStandFuelEvent.class, "setFuelPower", f -> f.param("power", Integer.class).returns(Void.TYPE).invoke(a -> { e(a).setFuelPower(NovaTypeSupport.argument(a, 1, Integer.class)); return null; }));
        b.extension(BrewingStandFuelEvent.class, "isConsuming", f -> f.returns(Boolean.class).invoke(a -> e(a).isConsuming()));
        b.extension(BrewingStandFuelEvent.class, "setConsuming", f -> f.param("consuming", Boolean.class).returns(Void.TYPE).invoke(a -> { e(a).setConsuming(NovaTypeSupport.argument(a, 1, Boolean.class)); return null; }));
    }
    private static BrewingStandFuelEvent e(Object[] a) { return NovaTypeSupport.argument(a, 0, BrewingStandFuelEvent.class); }
}
