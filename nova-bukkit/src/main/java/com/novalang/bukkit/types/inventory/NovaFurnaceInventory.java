package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

@Requires(classes = {"org.bukkit.inventory.FurnaceInventory"})
public final class NovaFurnaceInventory {
    private NovaFurnaceInventory() { }
    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef item = JavaTypeRef.javaType(ItemStack.class).nullable();
        builder.extension(FurnaceInventory.class, "result", function -> function.returns(item).invoke(arguments -> event(arguments).getResult()));
        builder.extension(FurnaceInventory.class, "fuel", function -> function.returns(item).invoke(arguments -> event(arguments).getFuel()));
        builder.extension(FurnaceInventory.class, "smelting", function -> function.returns(item).invoke(arguments -> event(arguments).getSmelting()));
        builder.extension(FurnaceInventory.class, "setResult", function -> function.param("item", item).returns(Void.TYPE).invoke(arguments -> { event(arguments).setResult(NovaTypeSupport.argument(arguments, 1, ItemStack.class)); return null; }));
        builder.extension(FurnaceInventory.class, "setFuel", function -> function.param("item", item).returns(Void.TYPE).invoke(arguments -> { event(arguments).setFuel(NovaTypeSupport.argument(arguments, 1, ItemStack.class)); return null; }));
        builder.extension(FurnaceInventory.class, "setSmelting", function -> function.param("item", item).returns(Void.TYPE).invoke(arguments -> { event(arguments).setSmelting(NovaTypeSupport.argument(arguments, 1, ItemStack.class)); return null; }));
        builder.extension(FurnaceInventory.class, "holder", function -> function.returns(JavaTypeRef.javaType(Furnace.class).nullable()).invoke(arguments -> event(arguments).getHolder()));
    }
    private static FurnaceInventory event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, FurnaceInventory.class);
    }
}
