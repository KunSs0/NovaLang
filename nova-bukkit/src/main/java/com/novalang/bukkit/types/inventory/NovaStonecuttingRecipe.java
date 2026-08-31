package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/** 1.14+ StonecuttingRecipe 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.inventory.StonecuttingRecipe", "org.bukkit.inventory.RecipeChoice"}, methods = {
        "org.bukkit.inventory.StonecuttingRecipe#setInput",
        "org.bukkit.inventory.StonecuttingRecipe#getInput",
        "org.bukkit.inventory.StonecuttingRecipe#setInputChoice",
        "org.bukkit.inventory.StonecuttingRecipe#getInputChoice",
        "org.bukkit.inventory.StonecuttingRecipe#getResult",
        "org.bukkit.inventory.StonecuttingRecipe#getKey",
        "org.bukkit.inventory.StonecuttingRecipe#getGroup",
        "org.bukkit.inventory.StonecuttingRecipe#setGroup"})
public final class NovaStonecuttingRecipe {
    private static final String TYPE = "org.bukkit.inventory.StonecuttingRecipe";
    private static final String CHOICE = "org.bukkit.inventory.RecipeChoice";
    private NovaStonecuttingRecipe() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaInventoryReflection.type(NovaStonecuttingRecipe.class, TYPE);
        Class<?> choice = NovaInventoryReflection.type(NovaStonecuttingRecipe.class, CHOICE);
        Method setInput = NovaInventoryReflection.method(type, "setInput", Material.class);
        Method getInput = NovaInventoryReflection.method(type, "getInput");
        Method setInputChoice = NovaInventoryReflection.method(type, "setInputChoice", choice);
        Method getInputChoice = NovaInventoryReflection.method(type, "getInputChoice");
        Method getResult = NovaInventoryReflection.method(type, "getResult");
        Method getKey = NovaInventoryReflection.method(type, "getKey");
        Method getGroup = NovaInventoryReflection.method(type, "getGroup");
        Method setGroup = NovaInventoryReflection.method(type, "setGroup", String.class);
        builder.extension(type, "setInput", function -> function.param("material", Material.class).returns(JavaTypeRef.javaType(type)).invoke(arguments -> NovaInventoryReflection.invoke(setInput, arguments[0], arguments[1])));
        builder.extension(type, "input", function -> function.returns(ItemStack.class).invoke(arguments -> NovaInventoryReflection.invoke(getInput, arguments[0])));
        builder.extension(type, "setInputChoice", function -> function.param("choice", JavaTypeRef.javaType(choice)).returns(JavaTypeRef.javaType(type)).invoke(arguments -> NovaInventoryReflection.invoke(setInputChoice, arguments[0], arguments[1])));
        builder.extension(type, "inputChoice", function -> function.returns(JavaTypeRef.javaType(choice)).invoke(arguments -> NovaInventoryReflection.invoke(getInputChoice, arguments[0])));
        builder.extension(type, "result", function -> function.returns(ItemStack.class).invoke(arguments -> NovaInventoryReflection.invoke(getResult, arguments[0])));
        builder.extension(type, "key", function -> function.returns(NamespacedKey.class).invoke(arguments -> NovaInventoryReflection.invoke(getKey, arguments[0])));
        builder.extension(type, "group", function -> function.returns(String.class).invoke(arguments -> NovaInventoryReflection.invoke(getGroup, arguments[0])));
        builder.extension(type, "setGroup", function -> function.param("group", String.class).returns(Void.TYPE).invoke(arguments -> NovaInventoryReflection.invoke(setGroup, arguments[0], arguments[1])));
    }
}
