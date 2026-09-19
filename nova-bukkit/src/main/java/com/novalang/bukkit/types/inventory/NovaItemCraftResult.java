package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Map;

/** 1.20.5+ ItemCraftResult 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.inventory.ItemCraftResult"}, methods = {
        "org.bukkit.inventory.ItemCraftResult#getResult",
        "org.bukkit.inventory.ItemCraftResult#getResultingMatrix",
        "org.bukkit.inventory.ItemCraftResult#getOverflowItems"})
public final class NovaItemCraftResult {
    private static final String TYPE = "org.bukkit.inventory.ItemCraftResult";
    private NovaItemCraftResult() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaInventoryReflection.type(NovaItemCraftResult.class, TYPE);
        Method getResult = NovaInventoryReflection.method(type, "getResult");
        Method getResultingMatrix = NovaInventoryReflection.method(type, "getResultingMatrix");
        Method getOverflowItems = NovaInventoryReflection.method(type, "getOverflowItems");
        builder.extension(type, "result", function -> function.returns(ItemStack.class).invoke(arguments -> NovaInventoryReflection.invoke(getResult, arguments[0])));
        builder.extension(type, "resultingMatrix", function -> function.returns(ItemStack[].class).invoke(arguments -> NovaInventoryReflection.invoke(getResultingMatrix, arguments[0])));
        builder.extension(type, "overflowItems", function -> function.returns(Map.class).invoke(arguments -> NovaInventoryReflection.invoke(getOverflowItems, arguments[0])));
    }
}
