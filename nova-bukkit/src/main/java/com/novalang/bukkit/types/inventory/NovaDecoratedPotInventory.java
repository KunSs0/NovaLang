package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/** 1.20+ DecoratedPotInventory 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.inventory.DecoratedPotInventory", "org.bukkit.block.DecoratedPot"}, methods = {
        "org.bukkit.inventory.DecoratedPotInventory#setItem",
        "org.bukkit.inventory.DecoratedPotInventory#getItem",
        "org.bukkit.inventory.DecoratedPotInventory#getHolder"})
public final class NovaDecoratedPotInventory {
    private static final String INVENTORY = "org.bukkit.inventory.DecoratedPotInventory";
    private static final String HOLDER = "org.bukkit.block.DecoratedPot";
    private NovaDecoratedPotInventory() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> inventory = NovaInventoryReflection.type(NovaDecoratedPotInventory.class, INVENTORY);
        Class<?> holder = NovaInventoryReflection.type(NovaDecoratedPotInventory.class, HOLDER);
        Method setItem = NovaInventoryReflection.method(inventory, "setItem", ItemStack.class);
        Method getItem = NovaInventoryReflection.method(inventory, "getItem");
        Method getHolder = NovaInventoryReflection.method(inventory, "getHolder");
        builder.extension(inventory, "setItem", function -> function.param("item", ItemStack.class).returns(Void.TYPE).invoke(arguments -> NovaInventoryReflection.invoke(setItem, arguments[0], arguments[1])));
        builder.extension(inventory, "item", function -> function.returns(JavaTypeRef.javaType(ItemStack.class).nullable()).invoke(arguments -> NovaInventoryReflection.invoke(getItem, arguments[0])));
        builder.extension(inventory, "holder", function -> function.returns(JavaTypeRef.javaType(holder).nullable()).invoke(arguments -> NovaInventoryReflection.invoke(getHolder, arguments[0])));
    }
}
