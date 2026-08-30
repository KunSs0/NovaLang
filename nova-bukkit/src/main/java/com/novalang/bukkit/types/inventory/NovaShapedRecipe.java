package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;

/** Spigot 1.12.2 ShapedRecipe 的 Fluxon 函数别名。 */
final class NovaShapedRecipe {

    private NovaShapedRecipe() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(ShapedRecipe.class, "shape", function -> function.returns(String[].class)
                .invoke(arguments -> recipe(arguments).getShape()));
        builder.extension(ShapedRecipe.class, "setIngredient", function -> function.param("key", String.class)
                .param("ingredient", MaterialData.class).returns(ShapedRecipe.class)
                .invoke(arguments -> recipe(arguments).setIngredient(key(arguments), argument(arguments, 2, MaterialData.class))));
        builder.extension(ShapedRecipe.class, "setIngredient", function -> function.param("key", String.class)
                .param("ingredient", Material.class).returns(ShapedRecipe.class)
                .invoke(arguments -> recipe(arguments).setIngredient(key(arguments), argument(arguments, 2, Material.class))));
        builder.extension(ShapedRecipe.class, "setIngredient", function -> function.param("key", String.class)
                .param("ingredient", Material.class).param("data", Integer.class).returns(ShapedRecipe.class)
                .invoke(arguments -> recipe(arguments).setIngredient(key(arguments), argument(arguments, 2, Material.class), argument(arguments, 3, Integer.class))));
    }

    private static ShapedRecipe recipe(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ShapedRecipe.class);
    }

    private static char key(Object[] arguments) {
        String value = argument(arguments, 1, String.class);
        return value.charAt(0);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
