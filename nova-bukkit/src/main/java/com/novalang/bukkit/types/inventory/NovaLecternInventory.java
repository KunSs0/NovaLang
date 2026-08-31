package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.14+ LecternInventory 的 holder 别名。 */
@Requires(classes = {
        "org.bukkit.inventory.LecternInventory",
        "org.bukkit.block.Lectern"}, methods = {
        "org.bukkit.inventory.LecternInventory#getHolder"})
public final class NovaLecternInventory {

    private static final String INVENTORY = "org.bukkit.inventory.LecternInventory";
    private static final String HOLDER = "org.bukkit.block.Lectern";

    private NovaLecternInventory() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> inventoryType = NovaInventoryReflection.type(NovaLecternInventory.class, INVENTORY);
        Class<?> holderType = NovaInventoryReflection.type(NovaLecternInventory.class, HOLDER);
        Method getHolder = NovaInventoryReflection.method(inventoryType, "getHolder");
        builder.extension(inventoryType, "holder", function -> function
                .returns(JavaTypeRef.javaType(holderType).nullable())
                .invoke(arguments -> NovaInventoryReflection.invoke(getHolder, arguments[0])));
    }
}
