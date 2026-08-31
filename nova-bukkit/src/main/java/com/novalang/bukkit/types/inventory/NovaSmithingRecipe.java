package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/** 1.16+ SmithingRecipe 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.inventory.SmithingRecipe", "org.bukkit.inventory.RecipeChoice"}, methods = {
        "org.bukkit.inventory.SmithingRecipe#getBase",
        "org.bukkit.inventory.SmithingRecipe#getAddition",
        "org.bukkit.inventory.SmithingRecipe#getResult",
        "org.bukkit.inventory.SmithingRecipe#getKey"})
public final class NovaSmithingRecipe {
    private static final String TYPE = "org.bukkit.inventory.SmithingRecipe";
    private static final String CHOICE = "org.bukkit.inventory.RecipeChoice";
    private NovaSmithingRecipe() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaInventoryReflection.type(NovaSmithingRecipe.class, TYPE);
        Class<?> choice = NovaInventoryReflection.type(NovaSmithingRecipe.class, CHOICE);
        Method getBase = NovaInventoryReflection.method(type, "getBase");
        Method getAddition = NovaInventoryReflection.method(type, "getAddition");
        Method getResult = NovaInventoryReflection.method(type, "getResult");
        Method getKey = NovaInventoryReflection.method(type, "getKey");
        builder.extension(type, "base", function -> function.returns(JavaTypeRef.javaType(choice)).invoke(arguments -> NovaInventoryReflection.invoke(getBase, arguments[0])));
        builder.extension(type, "addition", function -> function.returns(JavaTypeRef.javaType(choice)).invoke(arguments -> NovaInventoryReflection.invoke(getAddition, arguments[0])));
        builder.extension(type, "result", function -> function.returns(ItemStack.class).invoke(arguments -> NovaInventoryReflection.invoke(getResult, arguments[0])));
        builder.extension(type, "key", function -> function.returns(NamespacedKey.class).invoke(arguments -> NovaInventoryReflection.invoke(getKey, arguments[0])));
    }
}
