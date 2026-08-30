package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.FurnaceInventory;

/** Furnace 方块状态的 Spigot 1.12.2 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.block.Furnace"})
final class NovaFurnace {

    private NovaFurnace() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Furnace.class, "burnTime", function -> function.returns(Integer.class)
                .invoke(arguments -> (int) furnace(arguments).getBurnTime()));
        builder.extension(Furnace.class, "setBurnTime", function -> function.param("ticks", Integer.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    furnace(arguments).setBurnTime(NovaTypeSupport.argument(arguments, 1, Integer.class).shortValue());
                    return null;
                }));
        builder.extension(Furnace.class, "cookTime", function -> function.returns(Integer.class)
                .invoke(arguments -> (int) furnace(arguments).getCookTime()));
        builder.extension(Furnace.class, "setCookTime", function -> function.param("ticks", Integer.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    furnace(arguments).setCookTime(NovaTypeSupport.argument(arguments, 1, Integer.class).shortValue());
                    return null;
                }));
        builder.extension(Furnace.class, "inventory", function -> function.returns(FurnaceInventory.class)
                .invoke(arguments -> furnace(arguments).getInventory()));
        builder.extension(Furnace.class, "snapshotInventory", function -> function.returns(FurnaceInventory.class)
                .invoke(arguments -> furnace(arguments).getSnapshotInventory()));
    }

    private static Furnace furnace(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Furnace.class);
    }
}
