package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/** 1.20.5+ ToolComponent 及 ToolRule 的 Fluxon 函数契约。 */
@Requires(classes = {
        "org.bukkit.inventory.meta.components.ToolComponent",
        "org.bukkit.inventory.meta.components.ToolComponent$ToolRule",
        "org.bukkit.Tag"}, methods = {
        "org.bukkit.inventory.meta.components.ToolComponent#getDefaultMiningSpeed",
        "org.bukkit.inventory.meta.components.ToolComponent#setDefaultMiningSpeed",
        "org.bukkit.inventory.meta.components.ToolComponent#getDamagePerBlock",
        "org.bukkit.inventory.meta.components.ToolComponent#setDamagePerBlock",
        "org.bukkit.inventory.meta.components.ToolComponent#getRules",
        "org.bukkit.inventory.meta.components.ToolComponent#setRules",
        "org.bukkit.inventory.meta.components.ToolComponent#addRule",
        "org.bukkit.inventory.meta.components.ToolComponent#removeRule",
        "org.bukkit.inventory.meta.components.ToolComponent$ToolRule#getBlocks",
        "org.bukkit.inventory.meta.components.ToolComponent$ToolRule#setBlocks",
        "org.bukkit.inventory.meta.components.ToolComponent$ToolRule#getSpeed",
        "org.bukkit.inventory.meta.components.ToolComponent$ToolRule#setSpeed",
        "org.bukkit.inventory.meta.components.ToolComponent$ToolRule#isCorrectForDrops",
        "org.bukkit.inventory.meta.components.ToolComponent$ToolRule#setCorrectForDrops"})
public final class NovaToolComponent {

    private static final String TOOL_COMPONENT = "org.bukkit.inventory.meta.components.ToolComponent";
    private static final String TOOL_RULE = "org.bukkit.inventory.meta.components.ToolComponent$ToolRule";
    private static final String TAG = "org.bukkit.Tag";

    private NovaToolComponent() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> toolComponent = NovaInventoryReflection.type(NovaToolComponent.class, TOOL_COMPONENT);
        Class<?> toolRule = NovaInventoryReflection.type(NovaToolComponent.class, TOOL_RULE);
        Class<?> tag = NovaInventoryReflection.type(NovaToolComponent.class, TAG);

        Method getDefaultMiningSpeed = NovaInventoryReflection.method(toolComponent, "getDefaultMiningSpeed");
        Method setDefaultMiningSpeed = NovaInventoryReflection.method(toolComponent, "setDefaultMiningSpeed", Float.TYPE);
        Method getDamagePerBlock = NovaInventoryReflection.method(toolComponent, "getDamagePerBlock");
        Method setDamagePerBlock = NovaInventoryReflection.method(toolComponent, "setDamagePerBlock", Integer.TYPE);
        Method getRules = NovaInventoryReflection.method(toolComponent, "getRules");
        Method setRules = NovaInventoryReflection.method(toolComponent, "setRules", List.class);
        Method addMaterialRule = NovaInventoryReflection.method(toolComponent, "addRule", Material.class, Float.TYPE, Boolean.TYPE);
        Method addCollectionRule = NovaInventoryReflection.method(toolComponent, "addRule", Collection.class, Float.TYPE, Boolean.TYPE);
        Method addTagRule = NovaInventoryReflection.method(toolComponent, "addRule", tag, Float.TYPE, Boolean.TYPE);
        Method removeRule = NovaInventoryReflection.method(toolComponent, "removeRule", toolRule);

        JavaTypeRef ruleList = JavaTypeRef.listOf(JavaTypeRef.javaType(toolRule));
        JavaTypeRef tagType = JavaTypeRef.javaType(tag);
        builder.extension(toolComponent, "defaultMiningSpeed", function -> function.returns(Float.class)
                .invoke(arguments -> NovaInventoryReflection.invoke(getDefaultMiningSpeed, arguments[0])));
        builder.extension(toolComponent, "setDefaultMiningSpeed", function -> function.param("speed", Float.class).returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(setDefaultMiningSpeed, arguments[0], arguments[1])));
        builder.extension(toolComponent, "damagePerBlock", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaInventoryReflection.invoke(getDamagePerBlock, arguments[0])));
        builder.extension(toolComponent, "setDamagePerBlock", function -> function.param("damage", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(setDamagePerBlock, arguments[0], arguments[1])));
        builder.extension(toolComponent, "rules", function -> function.returns(ruleList)
                .invoke(arguments -> NovaInventoryReflection.invoke(getRules, arguments[0])));
        builder.extension(toolComponent, "setRules", function -> function.param("rules", ruleList).returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(setRules, arguments[0], arguments[1])));
        builder.extension(toolComponent, "addRule", function -> function.param("material", Material.class).param("speed", Float.class).param("correctForDrops", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(addMaterialRule, arguments[0], arguments[1], arguments[2], arguments[3])));
        builder.extension(toolComponent, "addRule", function -> function.param("materials", Collection.class).param("speed", Float.class).param("correctForDrops", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(addCollectionRule, arguments[0], arguments[1], arguments[2], arguments[3])));
        builder.extension(toolComponent, "addRule", function -> function.param("tag", tagType).param("speed", Float.class).param("correctForDrops", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(addTagRule, arguments[0], arguments[1], arguments[2], arguments[3])));
        builder.extension(toolComponent, "removeRule", function -> function.param("rule", toolRule).returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(removeRule, arguments[0], arguments[1])));

        registerRule(builder, toolRule, tag);
    }

    private static void registerRule(JavaTypes.Builder builder, Class<?> toolRule, Class<?> tag) {
        Method getBlocks = NovaInventoryReflection.method(toolRule, "getBlocks");
        Method setMaterialBlocks = NovaInventoryReflection.method(toolRule, "setBlocks", Material.class);
        Method setCollectionBlocks = NovaInventoryReflection.method(toolRule, "setBlocks", Collection.class);
        Method setTagBlocks = NovaInventoryReflection.method(toolRule, "setBlocks", tag);
        Method getSpeed = NovaInventoryReflection.method(toolRule, "getSpeed");
        Method setSpeed = NovaInventoryReflection.method(toolRule, "setSpeed", Float.TYPE);
        Method isCorrectForDrops = NovaInventoryReflection.method(toolRule, "isCorrectForDrops");
        Method setCorrectForDrops = NovaInventoryReflection.method(toolRule, "setCorrectForDrops", Boolean.TYPE);
        JavaTypeRef tagType = JavaTypeRef.javaType(tag);

        builder.extension(toolRule, "blocks", function -> function.returns(Collection.class)
                .invoke(arguments -> NovaInventoryReflection.invoke(getBlocks, arguments[0])));
        builder.extension(toolRule, "setBlocks", function -> function.param("material", Material.class).returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(setMaterialBlocks, arguments[0], arguments[1])));
        builder.extension(toolRule, "setBlocks", function -> function.param("materials", Collection.class).returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(setCollectionBlocks, arguments[0], arguments[1])));
        builder.extension(toolRule, "setBlocks", function -> function.param("tag", tagType).returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(setTagBlocks, arguments[0], arguments[1])));
        builder.extension(toolRule, "speed", function -> function.returns(Float.class)
                .invoke(arguments -> NovaInventoryReflection.invoke(getSpeed, arguments[0])));
        builder.extension(toolRule, "setSpeed", function -> function.param("speed", Float.class).returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(setSpeed, arguments[0], arguments[1])));
        builder.extension(toolRule, "isCorrectForDrops", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaInventoryReflection.invoke(isCorrectForDrops, arguments[0])));
        builder.extension(toolRule, "setCorrectForDrops", function -> function.param("correctForDrops", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> NovaInventoryReflection.invoke(setCorrectForDrops, arguments[0], arguments[1])));
    }
}
