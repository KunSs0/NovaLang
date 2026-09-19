package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;

/** BrewingStand 方块状态的 Spigot 1.12.2 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.block.BrewingStand"})
final class NovaBrewingStand {

    private NovaBrewingStand() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(BrewingStand.class, "brewingTime", function -> function.returns(Integer.class)
                .invoke(arguments -> brewingStand(arguments).getBrewingTime()));
        builder.extension(BrewingStand.class, "setBrewingTime", function -> function.param("ticks", Integer.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    brewingStand(arguments).setBrewingTime(NovaTypeSupport.argument(arguments, 1, Integer.class));
                    return null;
                }));
        builder.extension(BrewingStand.class, "fuelLevel", function -> function.returns(Integer.class)
                .invoke(arguments -> brewingStand(arguments).getFuelLevel()));
        builder.extension(BrewingStand.class, "setFuelLevel", function -> function.param("level", Integer.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    brewingStand(arguments).setFuelLevel(NovaTypeSupport.argument(arguments, 1, Integer.class));
                    return null;
                }));
        builder.extension(BrewingStand.class, "inventory", function -> function.returns(BrewerInventory.class)
                .invoke(arguments -> brewingStand(arguments).getInventory()));
        builder.extension(BrewingStand.class, "snapshotInventory", function -> function.returns(BrewerInventory.class)
                .invoke(arguments -> brewingStand(arguments).getSnapshotInventory()));
    }

    private static BrewingStand brewingStand(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BrewingStand.class);
    }
}
