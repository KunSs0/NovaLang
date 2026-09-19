package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

/** Spigot 1.12.2 中酿造台库存的 Fluxon 函数别名。 */
@Requires(classes = {"org.bukkit.inventory.BrewerInventory"})
public final class NovaBrewerInventory {

    private NovaBrewerInventory() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef item = JavaTypeRef.javaType(ItemStack.class).nullable();
        builder.extension(BrewerInventory.class, "ingredient", function -> function
                .returns(item)
                .invoke(arguments -> brewerInventory(arguments).getIngredient()));
        builder.extension(BrewerInventory.class, "setIngredient", function -> function
                .param("item", item)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    brewerInventory(arguments).setIngredient(
                            NovaTypeSupport.argument(arguments, 1, ItemStack.class));
                    return null;
                }));
        builder.extension(BrewerInventory.class, "fuel", function -> function
                .returns(item)
                .invoke(arguments -> brewerInventory(arguments).getFuel()));
        builder.extension(BrewerInventory.class, "setFuel", function -> function
                .param("item", item)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    brewerInventory(arguments).setFuel(
                            NovaTypeSupport.argument(arguments, 1, ItemStack.class));
                    return null;
                }));
        builder.extension(BrewerInventory.class, "holder", function -> function
                .returns(JavaTypeRef.javaType(BrewingStand.class).nullable())
                .invoke(arguments -> brewerInventory(arguments).getHolder()));
    }

    private static BrewerInventory brewerInventory(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BrewerInventory.class);
    }
}
