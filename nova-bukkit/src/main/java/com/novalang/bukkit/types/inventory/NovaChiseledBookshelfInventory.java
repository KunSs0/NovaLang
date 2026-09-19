package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.20+ ChiseledBookshelfInventory 的 holder 别名。 */
@Requires(classes = {
        "org.bukkit.inventory.ChiseledBookshelfInventory",
        "org.bukkit.block.ChiseledBookshelf"}, methods = {
        "org.bukkit.inventory.ChiseledBookshelfInventory#getHolder"})
public final class NovaChiseledBookshelfInventory {

    private static final String INVENTORY = "org.bukkit.inventory.ChiseledBookshelfInventory";
    private static final String HOLDER = "org.bukkit.block.ChiseledBookshelf";

    private NovaChiseledBookshelfInventory() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> inventoryType = NovaInventoryReflection.type(NovaChiseledBookshelfInventory.class, INVENTORY);
        Class<?> holderType = NovaInventoryReflection.type(NovaChiseledBookshelfInventory.class, HOLDER);
        Method getHolder = NovaInventoryReflection.method(inventoryType, "getHolder");
        builder.extension(inventoryType, "holder", function -> function
                .returns(JavaTypeRef.javaType(holderType).nullable())
                .invoke(arguments -> NovaInventoryReflection.invoke(getHolder, arguments[0])));
    }
}
