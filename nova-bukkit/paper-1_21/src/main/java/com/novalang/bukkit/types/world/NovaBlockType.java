package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.World;

import java.lang.reflect.Method;

/** 1.20.5+ BlockType 与 BlockType.Typed 的 Fluxon 函数契约。 */
@Requires(classes = {
        "org.bukkit.block.BlockType",
        "org.bukkit.block.BlockType$Typed",
        "org.bukkit.inventory.ItemType",
        "org.bukkit.block.data.BlockData"}, methods = {
        "org.bukkit.block.BlockType#typed",
        "org.bukkit.block.BlockType#hasItemType",
        "org.bukkit.block.BlockType#getItemType",
        "org.bukkit.block.BlockType#createBlockData",
        "org.bukkit.block.BlockType#isSolid",
        "org.bukkit.block.BlockType#isFlammable",
        "org.bukkit.block.BlockType#isBurnable",
        "org.bukkit.block.BlockType#isOccluding",
        "org.bukkit.block.BlockType#hasGravity",
        "org.bukkit.block.BlockType#isInteractable",
        "org.bukkit.block.BlockType#getHardness",
        "org.bukkit.block.BlockType#getBlastResistance",
        "org.bukkit.block.BlockType#getSlipperiness",
        "org.bukkit.block.BlockType#isAir",
        "org.bukkit.block.BlockType#isEnabledByFeature",
        "org.bukkit.block.BlockType#asMaterial",
        "org.bukkit.block.BlockType#getBlockDataClass",
        "org.bukkit.block.BlockType$Typed#getBlockDataClass",
        "org.bukkit.block.BlockType$Typed#createBlockData"})
public final class NovaBlockType {

    private static final String BLOCK_TYPE = "org.bukkit.block.BlockType";
    private static final String TYPED = "org.bukkit.block.BlockType$Typed";
    private static final String ITEM_TYPE = "org.bukkit.inventory.ItemType";
    private static final String BLOCK_DATA = "org.bukkit.block.data.BlockData";

    private NovaBlockType() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> blockType = NovaBlockDataReflection.type(NovaBlockType.class, BLOCK_TYPE);
        Class<?> typedType = NovaBlockDataReflection.type(NovaBlockType.class, TYPED);
        Class<?> itemType = NovaBlockDataReflection.type(NovaBlockType.class, ITEM_TYPE);
        Class<?> blockData = NovaBlockDataReflection.type(NovaBlockType.class, BLOCK_DATA);
        registerBlockType(builder, blockType, typedType, itemType, blockData);
        registerTyped(builder, typedType, blockData);
    }

    private static void registerBlockType(JavaTypes.Builder builder, Class<?> blockType, Class<?> typedType,
                                          Class<?> itemType, Class<?> blockData) {
        Method typed = NovaBlockDataReflection.method(blockType, "typed");
        Method hasItemType = NovaBlockDataReflection.method(blockType, "hasItemType");
        Method getItemType = NovaBlockDataReflection.method(blockType, "getItemType");
        Method createBlockData = NovaBlockDataReflection.method(blockType, "createBlockData");
        Method createBlockDataWithData = NovaBlockDataReflection.method(blockType, "createBlockData", String.class);
        Method isSolid = NovaBlockDataReflection.method(blockType, "isSolid");
        Method isFlammable = NovaBlockDataReflection.method(blockType, "isFlammable");
        Method isBurnable = NovaBlockDataReflection.method(blockType, "isBurnable");
        Method isOccluding = NovaBlockDataReflection.method(blockType, "isOccluding");
        Method hasGravity = NovaBlockDataReflection.method(blockType, "hasGravity");
        Method isInteractable = NovaBlockDataReflection.method(blockType, "isInteractable");
        Method getHardness = NovaBlockDataReflection.method(blockType, "getHardness");
        Method getBlastResistance = NovaBlockDataReflection.method(blockType, "getBlastResistance");
        Method getSlipperiness = NovaBlockDataReflection.method(blockType, "getSlipperiness");
        Method isAir = NovaBlockDataReflection.method(blockType, "isAir");
        Method isEnabledByFeature = NovaBlockDataReflection.method(blockType, "isEnabledByFeature", World.class);
        Method asMaterial = NovaBlockDataReflection.method(blockType, "asMaterial");
        Method getBlockDataClass = NovaBlockDataReflection.method(blockType, "getBlockDataClass");

        builder.extension(blockType, "typed", function -> function.returns(JavaTypeRef.javaType(typedType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(typed, arguments[0])));
        builder.extension(blockType, "hasItemType", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(hasItemType, arguments[0])));
        builder.extension(blockType, "itemType", function -> function.returns(JavaTypeRef.javaType(itemType).nullable())
                .invoke(arguments -> NovaBlockDataReflection.invoke(getItemType, arguments[0])));
        registerBlockDataFactories(builder, blockType, blockData, createBlockData, createBlockDataWithData);
        builder.extension(blockType, "isSolid", function -> function.returns(Boolean.class).invoke(arguments -> NovaBlockDataReflection.invoke(isSolid, arguments[0])));
        builder.extension(blockType, "isFlammable", function -> function.returns(Boolean.class).invoke(arguments -> NovaBlockDataReflection.invoke(isFlammable, arguments[0])));
        builder.extension(blockType, "isBurnable", function -> function.returns(Boolean.class).invoke(arguments -> NovaBlockDataReflection.invoke(isBurnable, arguments[0])));
        builder.extension(blockType, "isOccluding", function -> function.returns(Boolean.class).invoke(arguments -> NovaBlockDataReflection.invoke(isOccluding, arguments[0])));
        builder.extension(blockType, "hasGravity", function -> function.returns(Boolean.class).invoke(arguments -> NovaBlockDataReflection.invoke(hasGravity, arguments[0])));
        builder.extension(blockType, "isInteractable", function -> function.returns(Boolean.class).invoke(arguments -> NovaBlockDataReflection.invoke(isInteractable, arguments[0])));
        builder.extension(blockType, "hardness", function -> function.returns(Float.class).invoke(arguments -> NovaBlockDataReflection.invoke(getHardness, arguments[0])));
        builder.extension(blockType, "blastResistance", function -> function.returns(Float.class).invoke(arguments -> NovaBlockDataReflection.invoke(getBlastResistance, arguments[0])));
        builder.extension(blockType, "slipperiness", function -> function.returns(Float.class).invoke(arguments -> NovaBlockDataReflection.invoke(getSlipperiness, arguments[0])));
        builder.extension(blockType, "isAir", function -> function.returns(Boolean.class).invoke(arguments -> NovaBlockDataReflection.invoke(isAir, arguments[0])));
        builder.extension(blockType, "isEnabledByFeature", function -> function.param("world", World.class).returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(isEnabledByFeature, arguments[0], arguments[1])));
        builder.extension(blockType, "asMaterial", function -> function.returns(Material.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(asMaterial, arguments[0])));
        builder.extension(blockType, "blockDataClass", function -> function.returns(Class.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getBlockDataClass, arguments[0])));
    }

    private static void registerTyped(JavaTypes.Builder builder, Class<?> typedType, Class<?> blockData) {
        Method getBlockDataClass = NovaBlockDataReflection.method(typedType, "getBlockDataClass");
        Method createBlockData = NovaBlockDataReflection.method(typedType, "createBlockData");
        Method createBlockDataWithData = NovaBlockDataReflection.method(typedType, "createBlockData", String.class);

        builder.extension(typedType, "blockDataClass", function -> function.returns(Class.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getBlockDataClass, arguments[0])));
        registerBlockDataFactories(builder, typedType, blockData, createBlockData, createBlockDataWithData);
    }

    private static void registerBlockDataFactories(JavaTypes.Builder builder, Class<?> owner, Class<?> blockData,
                                                   Method createBlockData, Method createBlockDataWithData) {
        builder.extension(owner, "createBlockData", function -> function.returns(JavaTypeRef.javaType(blockData))
                .invoke(arguments -> NovaBlockDataReflection.invoke(createBlockData, arguments[0])));
        builder.extension(owner, "createBlockData", function -> function.param("data", String.class)
                .returns(JavaTypeRef.javaType(blockData))
                .invoke(arguments -> NovaBlockDataReflection.invoke(createBlockDataWithData, arguments[0], arguments[1])));
    }
}
