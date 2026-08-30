package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

/** Spigot 1.12.2 ShapelessRecipe 的 Fluxon 函数别名。 */
final class NovaShapelessRecipe {

    private NovaShapelessRecipe() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(ShapelessRecipe.class, "addIngredient", function -> function.param("ingredient", MaterialData.class).returns(ShapelessRecipe.class)
                .invoke(arguments -> recipe(arguments).addIngredient(argument(arguments, 1, MaterialData.class))));
        builder.extension(ShapelessRecipe.class, "addIngredient", function -> function.param("ingredient", Material.class).returns(ShapelessRecipe.class)
                .invoke(arguments -> recipe(arguments).addIngredient(argument(arguments, 1, Material.class))));
        builder.extension(ShapelessRecipe.class, "addIngredient", function -> function.param("ingredient", Material.class).param("data", Integer.class).returns(ShapelessRecipe.class)
                .invoke(arguments -> recipe(arguments).addIngredient(argument(arguments, 1, Material.class), argument(arguments, 2, Integer.class))));
        builder.extension(ShapelessRecipe.class, "addIngredient", function -> function.param("count", Integer.class).param("ingredient", MaterialData.class).returns(ShapelessRecipe.class)
                .invoke(arguments -> recipe(arguments).addIngredient(argument(arguments, 1, Integer.class), argument(arguments, 2, MaterialData.class))));
        builder.extension(ShapelessRecipe.class, "addIngredient", function -> function.param("count", Integer.class).param("ingredient", Material.class).returns(ShapelessRecipe.class)
                .invoke(arguments -> recipe(arguments).addIngredient(argument(arguments, 1, Integer.class), argument(arguments, 2, Material.class))));
        builder.extension(ShapelessRecipe.class, "addIngredient", function -> function.param("count", Integer.class).param("ingredient", Material.class).param("data", Integer.class).returns(ShapelessRecipe.class)
                .invoke(arguments -> recipe(arguments).addIngredient(argument(arguments, 1, Integer.class), argument(arguments, 2, Material.class), argument(arguments, 3, Integer.class))));
        builder.extension(ShapelessRecipe.class, "removeIngredient", function -> function.param("ingredient", MaterialData.class).returns(ShapelessRecipe.class)
                .invoke(arguments -> recipe(arguments).removeIngredient(argument(arguments, 1, MaterialData.class))));
        builder.extension(ShapelessRecipe.class, "removeIngredient", function -> function.param("ingredient", Material.class).returns(ShapelessRecipe.class)
                .invoke(arguments -> recipe(arguments).removeIngredient(argument(arguments, 1, Material.class))));
        builder.extension(ShapelessRecipe.class, "removeIngredient", function -> function.param("ingredient", Material.class).param("data", Integer.class).returns(ShapelessRecipe.class)
                .invoke(arguments -> recipe(arguments).removeIngredient(argument(arguments, 1, Material.class), argument(arguments, 2, Integer.class))));
        builder.extension(ShapelessRecipe.class, "removeIngredient", function -> function.param("count", Integer.class).param("ingredient", MaterialData.class).returns(ShapelessRecipe.class)
                .invoke(arguments -> recipe(arguments).removeIngredient(argument(arguments, 1, Integer.class), argument(arguments, 2, MaterialData.class))));
        builder.extension(ShapelessRecipe.class, "removeIngredient", function -> function.param("count", Integer.class).param("ingredient", Material.class).returns(ShapelessRecipe.class)
                .invoke(arguments -> recipe(arguments).removeIngredient(argument(arguments, 1, Integer.class), argument(arguments, 2, Material.class))));
        builder.extension(ShapelessRecipe.class, "removeIngredient", function -> function.param("count", Integer.class).param("ingredient", Material.class).param("data", Integer.class).returns(ShapelessRecipe.class)
                .invoke(arguments -> recipe(arguments).removeIngredient(argument(arguments, 1, Integer.class), argument(arguments, 2, Material.class), argument(arguments, 3, Integer.class))));
        builder.extension(ShapelessRecipe.class, "ingredientList", function -> function.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(ItemStack.class)))
                .invoke(arguments -> recipe(arguments).getIngredientList()));
    }

    private static ShapelessRecipe recipe(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ShapelessRecipe.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
