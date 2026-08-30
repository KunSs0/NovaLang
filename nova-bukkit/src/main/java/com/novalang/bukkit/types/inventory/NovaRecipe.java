package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

@Requires(classes = {"org.bukkit.inventory.Recipe"})
public final class NovaRecipe {
    private NovaRecipe() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(Recipe.class, "result", function -> function.returns(ItemStack.class).invoke(arguments -> event(arguments).getResult()));
    }
    private static Recipe event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Recipe.class);
    }
}
