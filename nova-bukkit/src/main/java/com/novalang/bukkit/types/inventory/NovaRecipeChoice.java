package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.List;

/** 1.13+ RecipeChoice 及两个实现类型的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.inventory.RecipeChoice", "org.bukkit.inventory.RecipeChoice$MaterialChoice", "org.bukkit.inventory.RecipeChoice$ExactChoice"}, methods = {
        "org.bukkit.inventory.RecipeChoice#getItemStack", "org.bukkit.inventory.RecipeChoice#clone", "org.bukkit.inventory.RecipeChoice#test",
        "org.bukkit.inventory.RecipeChoice$MaterialChoice#getChoices", "org.bukkit.inventory.RecipeChoice$ExactChoice#getChoices"})
public final class NovaRecipeChoice {
    private static final String TYPE = "org.bukkit.inventory.RecipeChoice";
    private static final String MATERIAL_CHOICE = "org.bukkit.inventory.RecipeChoice$MaterialChoice";
    private static final String EXACT_CHOICE = "org.bukkit.inventory.RecipeChoice$ExactChoice";
    private NovaRecipeChoice() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaInventoryReflection.type(NovaRecipeChoice.class, TYPE);
        Class<?> materialChoice = NovaInventoryReflection.type(NovaRecipeChoice.class, MATERIAL_CHOICE);
        Class<?> exactChoice = NovaInventoryReflection.type(NovaRecipeChoice.class, EXACT_CHOICE);
        registerChoice(builder, type, false);
        registerChoice(builder, materialChoice, true);
        registerChoice(builder, exactChoice, true);
    }
    private static void registerChoice(JavaTypes.Builder builder, Class<?> type, boolean choices) {
        Method getItemStack = NovaInventoryReflection.method(type, "getItemStack");
        Method clone = NovaInventoryReflection.method(type, "clone");
        Method test = NovaInventoryReflection.method(type, "test", ItemStack.class);
        builder.extension(type, "itemStack", function -> function.returns(ItemStack.class).invoke(arguments -> NovaInventoryReflection.invoke(getItemStack, arguments[0])));
        builder.extension(type, "clone", function -> function.returns(JavaTypeRef.javaType(type)).invoke(arguments -> NovaInventoryReflection.invoke(clone, arguments[0])));
        builder.extension(type, "test", function -> function.param("item", ItemStack.class).returns(Boolean.class).invoke(arguments -> NovaInventoryReflection.invoke(test, arguments[0], arguments[1])));
        if (choices) {
            Method getChoices = NovaInventoryReflection.method(type, "getChoices");
            Method toString = NovaInventoryReflection.method(type, "toString");
            builder.extension(type, "choices", function -> function.returns(List.class).invoke(arguments -> NovaInventoryReflection.invoke(getChoices, arguments[0])));
            builder.extension(type, "toString", function -> function.returns(String.class).invoke(arguments -> NovaInventoryReflection.invoke(toString, arguments[0])));
        }
    }
}
