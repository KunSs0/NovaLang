package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

/** Spigot 1.12.2 DoubleChestInventory 接口扩展。 */
@Requires(classes = {"org.bukkit.inventory.DoubleChestInventory"})
public final class NovaDoubleChestInventory {
    private NovaDoubleChestInventory() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(DoubleChestInventory.class, "leftSide", function -> function
                .returns(Inventory.class)
                .invoke(arguments -> inventory(arguments).getLeftSide()));
        builder.extension(DoubleChestInventory.class, "rightSide", function -> function
                .returns(Inventory.class)
                .invoke(arguments -> inventory(arguments).getRightSide()));
        builder.extension(DoubleChestInventory.class, "holder", function -> function
                .returns(DoubleChest.class)
                .invoke(arguments -> inventory(arguments).getHolder()));
    }

    private static DoubleChestInventory inventory(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, DoubleChestInventory.class);
    }
}
