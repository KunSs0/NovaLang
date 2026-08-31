package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.20.5+ ItemType 的 Fluxon getter 别名。 */
@Requires(classes = {
        "org.bukkit.inventory.ItemType",
        "org.bukkit.block.BlockType",
        "org.bukkit.inventory.CreativeCategory"}, methods = {
        "org.bukkit.inventory.ItemType#getBlockType",
        "org.bukkit.inventory.ItemType#getMaxStackSize",
        "org.bukkit.inventory.ItemType#getMaxDurability",
        "org.bukkit.inventory.ItemType#getCompostChance",
        "org.bukkit.inventory.ItemType#getCraftingRemainingItem",
        "org.bukkit.inventory.ItemType#getCreativeCategory",
        "org.bukkit.inventory.ItemType#getItemMetaClass"})
public final class NovaItemType {

    private static final String ITEM_TYPE = "org.bukkit.inventory.ItemType";
    private static final String BLOCK_TYPE = "org.bukkit.block.BlockType";
    private static final String CREATIVE_CATEGORY = "org.bukkit.inventory.CreativeCategory";

    private NovaItemType() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> itemType = NovaInventoryReflection.type(NovaItemType.class, ITEM_TYPE);
        Class<?> blockType = NovaInventoryReflection.type(NovaItemType.class, BLOCK_TYPE);
        Class<?> creativeCategory = NovaInventoryReflection.type(NovaItemType.class, CREATIVE_CATEGORY);
        Method getBlockType = NovaInventoryReflection.method(itemType, "getBlockType");
        Method getMaxStackSize = NovaInventoryReflection.method(itemType, "getMaxStackSize");
        Method getMaxDurability = NovaInventoryReflection.method(itemType, "getMaxDurability");
        Method getCompostChance = NovaInventoryReflection.method(itemType, "getCompostChance");
        Method getCraftingRemainingItem = NovaInventoryReflection.method(itemType, "getCraftingRemainingItem");
        Method getCreativeCategory = NovaInventoryReflection.method(itemType, "getCreativeCategory");
        Method getItemMetaClass = NovaInventoryReflection.method(itemType, "getItemMetaClass");

        builder.extension(itemType, "blockType", function -> function
                .returns(JavaTypeRef.javaType(blockType).nullable())
                .invoke(arguments -> NovaInventoryReflection.invoke(getBlockType, arguments[0])));
        builder.extension(itemType, "maxStackSize", function -> function
                .returns(Integer.class)
                .invoke(arguments -> intValue(NovaInventoryReflection.invoke(getMaxStackSize, arguments[0]))));
        builder.extension(itemType, "maxDurability", function -> function
                .returns(Integer.class)
                .invoke(arguments -> intValue(NovaInventoryReflection.invoke(getMaxDurability, arguments[0]))));
        builder.extension(itemType, "compostChance", function -> function
                .returns(Float.class)
                .invoke(arguments -> floatValue(NovaInventoryReflection.invoke(getCompostChance, arguments[0]))));
        builder.extension(itemType, "craftingRemainingItem", function -> function
                .returns(JavaTypeRef.javaType(itemType).nullable())
                .invoke(arguments -> NovaInventoryReflection.invoke(getCraftingRemainingItem, arguments[0])));
        builder.extension(itemType, "creativeCategory", function -> function
                .returns(JavaTypeRef.javaType(creativeCategory).nullable())
                .invoke(arguments -> NovaInventoryReflection.invoke(getCreativeCategory, arguments[0])));
        builder.extension(itemType, "itemMetaClass", function -> function
                .returns(Class.class)
                .invoke(arguments -> NovaInventoryReflection.invoke(getItemMetaClass, arguments[0])));
    }

    private static int intValue(Object value) {
        return ((Number) value).intValue();
    }

    private static float floatValue(Object value) {
        return ((Number) value).floatValue();
    }
}
