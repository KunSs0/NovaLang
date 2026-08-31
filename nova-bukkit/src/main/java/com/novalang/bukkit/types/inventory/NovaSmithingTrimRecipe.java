package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.20+ SmithingTrimRecipe 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.inventory.SmithingTrimRecipe", "org.bukkit.inventory.RecipeChoice"}, methods = {
        "org.bukkit.inventory.SmithingTrimRecipe#getTemplate"})
public final class NovaSmithingTrimRecipe {
    private static final String TYPE = "org.bukkit.inventory.SmithingTrimRecipe";
    private static final String CHOICE = "org.bukkit.inventory.RecipeChoice";
    private NovaSmithingTrimRecipe() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaInventoryReflection.type(NovaSmithingTrimRecipe.class, TYPE);
        Class<?> choice = NovaInventoryReflection.type(NovaSmithingTrimRecipe.class, CHOICE);
        Method getTemplate = NovaInventoryReflection.method(type, "getTemplate");
        builder.extension(type, "template", function -> function.returns(JavaTypeRef.javaType(choice)).invoke(arguments -> NovaInventoryReflection.invoke(getTemplate, arguments[0])));
    }
}
