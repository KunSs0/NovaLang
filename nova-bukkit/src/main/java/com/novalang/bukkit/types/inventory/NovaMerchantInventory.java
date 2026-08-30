package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

@Requires(classes = {"org.bukkit.inventory.MerchantInventory"})
public final class NovaMerchantInventory {
    private NovaMerchantInventory() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(MerchantInventory.class, "selectedRecipeIndex", function -> function.returns(Integer.class).invoke(arguments -> event(arguments).getSelectedRecipeIndex()));
        builder.extension(MerchantInventory.class, "selectedRecipe", function -> function.returns(JavaTypeRef.javaType(MerchantRecipe.class).nullable()).invoke(arguments -> event(arguments).getSelectedRecipe()));
    }
    private static MerchantInventory event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, MerchantInventory.class);
    }
}
