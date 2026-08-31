package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/** 1.19+ JukeboxInventory 的 Fluxon 函数契约。 */
@Requires(classes = {
        "org.bukkit.inventory.JukeboxInventory",
        "org.bukkit.block.Jukebox"}, methods = {
        "org.bukkit.inventory.JukeboxInventory#setRecord",
        "org.bukkit.inventory.JukeboxInventory#getRecord",
        "org.bukkit.inventory.JukeboxInventory#getHolder"})
public final class NovaJukeboxInventory {

    private static final String INVENTORY = "org.bukkit.inventory.JukeboxInventory";
    private static final String HOLDER = "org.bukkit.block.Jukebox";

    private NovaJukeboxInventory() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> inventoryType = NovaInventoryReflection.type(NovaJukeboxInventory.class, INVENTORY);
        Class<?> holderType = NovaInventoryReflection.type(NovaJukeboxInventory.class, HOLDER);
        Method setRecord = NovaInventoryReflection.method(inventoryType, "setRecord", ItemStack.class);
        Method getRecord = NovaInventoryReflection.method(inventoryType, "getRecord");
        Method getHolder = NovaInventoryReflection.method(inventoryType, "getHolder");
        JavaTypeRef nullableItemStack = JavaTypeRef.javaType(ItemStack.class).nullable();

        builder.extension(inventoryType, "setRecord", function -> function
                .param("record", nullableItemStack)
                .returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(setRecord, arguments[0], arguments[1])));
        builder.extension(inventoryType, "record", function -> function
                .returns(nullableItemStack)
                .invoke(arguments -> NovaInventoryReflection.invoke(getRecord, arguments[0])));
        builder.extension(inventoryType, "holder", function -> function
                .returns(JavaTypeRef.javaType(holderType).nullable())
                .invoke(arguments -> NovaInventoryReflection.invoke(getHolder, arguments[0])));
    }
}
