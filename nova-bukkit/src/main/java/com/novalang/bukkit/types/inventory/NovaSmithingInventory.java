package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/** 1.16+ SmithingInventory 的 Fluxon 函数契约。 */
@Requires(classes = {
        "org.bukkit.inventory.SmithingInventory",
        "org.bukkit.inventory.Recipe"}, methods = {
        "org.bukkit.inventory.SmithingInventory#getResult",
        "org.bukkit.inventory.SmithingInventory#setResult",
        "org.bukkit.inventory.SmithingInventory#getRecipe"})
public final class NovaSmithingInventory {

    private static final String INVENTORY = "org.bukkit.inventory.SmithingInventory";
    private static final String RECIPE = "org.bukkit.inventory.Recipe";

    private NovaSmithingInventory() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> inventoryType = NovaInventoryReflection.type(NovaSmithingInventory.class, INVENTORY);
        Class<?> recipeType = NovaInventoryReflection.type(NovaSmithingInventory.class, RECIPE);
        Method getResult = NovaInventoryReflection.method(inventoryType, "getResult");
        Method setResult = NovaInventoryReflection.method(inventoryType, "setResult", ItemStack.class);
        Method getRecipe = NovaInventoryReflection.method(inventoryType, "getRecipe");
        JavaTypeRef nullableItemStack = JavaTypeRef.javaType(ItemStack.class).nullable();

        builder.extension(inventoryType, "result", function -> function
                .returns(nullableItemStack)
                .invoke(arguments -> NovaInventoryReflection.invoke(getResult, arguments[0])));
        builder.extension(inventoryType, "setResult", function -> function
                .param("result", nullableItemStack)
                .returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(setResult, arguments[0], arguments[1])));
        builder.extension(inventoryType, "recipe", function -> function
                .returns(JavaTypeRef.javaType(recipeType).nullable())
                .invoke(arguments -> NovaInventoryReflection.invoke(getRecipe, arguments[0])));
    }
}
