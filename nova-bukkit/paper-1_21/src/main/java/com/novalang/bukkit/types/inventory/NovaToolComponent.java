package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.inventory.PaperInventoryReflection;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/** 1.20.5+ ToolComponent 及 ToolRule 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.inventory.meta.components.ToolComponent", "org.bukkit.inventory.meta.components.ToolComponent$ToolRule", "org.bukkit.Tag"}, methods = {"org.bukkit.inventory.meta.components.ToolComponent#getDefaultMiningSpeed", "org.bukkit.inventory.meta.components.ToolComponent#setDefaultMiningSpeed", "org.bukkit.inventory.meta.components.ToolComponent#getDamagePerBlock", "org.bukkit.inventory.meta.components.ToolComponent#setDamagePerBlock", "org.bukkit.inventory.meta.components.ToolComponent#getRules", "org.bukkit.inventory.meta.components.ToolComponent#setRules", "org.bukkit.inventory.meta.components.ToolComponent#addRule(org.bukkit.Material,float,boolean)", "org.bukkit.inventory.meta.components.ToolComponent#addRule(java.util.Collection,float,boolean)", "org.bukkit.inventory.meta.components.ToolComponent#addRule(org.bukkit.Tag,float,boolean)", "org.bukkit.inventory.meta.components.ToolComponent#removeRule(org.bukkit.inventory.meta.components.ToolComponent$ToolRule)", "org.bukkit.inventory.meta.components.ToolComponent$ToolRule#getBlocks", "org.bukkit.inventory.meta.components.ToolComponent$ToolRule#setBlocks", "org.bukkit.inventory.meta.components.ToolComponent$ToolRule#getSpeed", "org.bukkit.inventory.meta.components.ToolComponent$ToolRule#setSpeed", "org.bukkit.inventory.meta.components.ToolComponent$ToolRule#isCorrectForDrops", "org.bukkit.inventory.meta.components.ToolComponent$ToolRule#setCorrectForDrops"})
public final class NovaToolComponent {
    private static final String TOOL_COMPONENT = "org.bukkit.inventory.meta.components.ToolComponent";
    private static final String TOOL_RULE = "org.bukkit.inventory.meta.components.ToolComponent$ToolRule";
    private static final String TAG = "org.bukkit.Tag";
    private NovaToolComponent() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> toolComponent = PaperInventoryReflection.type(NovaToolComponent.class, TOOL_COMPONENT);
        Class<?> toolRule = PaperInventoryReflection.type(NovaToolComponent.class, TOOL_RULE);
        Class<?> tag = PaperInventoryReflection.type(NovaToolComponent.class, TAG);
        Method getDefaultMiningSpeed = PaperInventoryReflection.method(toolComponent, "getDefaultMiningSpeed");
        Method setDefaultMiningSpeed = PaperInventoryReflection.method(toolComponent, "setDefaultMiningSpeed", Float.TYPE);
        Method getDamagePerBlock = PaperInventoryReflection.method(toolComponent, "getDamagePerBlock");
        Method setDamagePerBlock = PaperInventoryReflection.method(toolComponent, "setDamagePerBlock", Integer.TYPE);
        Method getRules = PaperInventoryReflection.method(toolComponent, "getRules");
        Method setRules = PaperInventoryReflection.method(toolComponent, "setRules", List.class);
        Method addMaterialRule = PaperInventoryReflection.method(toolComponent, "addRule", Material.class, Float.class, Boolean.class);
        Method addCollectionRule = PaperInventoryReflection.method(toolComponent, "addRule", Collection.class, Float.class, Boolean.class);
        Method addTagRule = PaperInventoryReflection.method(toolComponent, "addRule", tag, Float.class, Boolean.class);
        Method removeRule = PaperInventoryReflection.method(toolComponent, "removeRule", toolRule);
        JavaTypeRef ruleList = JavaTypeRef.listOf(JavaTypeRef.javaType(toolRule));
        JavaTypeRef tagType = JavaTypeRef.javaType(tag);
        builder.extension(toolComponent, "defaultMiningSpeed", function -> function.returns(Float.class).invoke(arguments -> PaperInventoryReflection.invoke(getDefaultMiningSpeed, arguments[0])));
        builder.extension(toolComponent, "setDefaultMiningSpeed", function -> function.param("speed", Float.class).returns(Void.TYPE).invoke(arguments -> PaperInventoryReflection.invoke(setDefaultMiningSpeed, arguments[0], arguments[1])));
        builder.extension(toolComponent, "damagePerBlock", function -> function.returns(Integer.class).invoke(arguments -> PaperInventoryReflection.invoke(getDamagePerBlock, arguments[0])));
        builder.extension(toolComponent, "setDamagePerBlock", function -> function.param("damage", Integer.class).returns(Void.TYPE).invoke(arguments -> PaperInventoryReflection.invoke(setDamagePerBlock, arguments[0], arguments[1])));
        builder.extension(toolComponent, "rules", function -> function.returns(ruleList).invoke(arguments -> PaperInventoryReflection.invoke(getRules, arguments[0])));
        builder.extension(toolComponent, "setRules", function -> function.param("rules", ruleList).returns(Void.TYPE).invoke(arguments -> PaperInventoryReflection.invoke(setRules, arguments[0], arguments[1])));
        builder.extension(toolComponent, "addRule", function -> function.param("material", Material.class).param("speed", Float.class).param("correctForDrops", Boolean.class).returns(Void.TYPE).invoke(arguments -> PaperInventoryReflection.invoke(addMaterialRule, arguments[0], arguments[1], arguments[2], arguments[3])));
        builder.extension(toolComponent, "addRule", function -> function.param("materials", Collection.class).param("speed", Float.class).param("correctForDrops", Boolean.class).returns(Void.TYPE).invoke(arguments -> PaperInventoryReflection.invoke(addCollectionRule, arguments[0], arguments[1], arguments[2], arguments[3])));
        builder.extension(toolComponent, "addRule", function -> function.param("tag", tagType).param("speed", Float.class).param("correctForDrops", Boolean.class).returns(Void.TYPE).invoke(arguments -> PaperInventoryReflection.invoke(addTagRule, arguments[0], arguments[1], arguments[2], arguments[3])));
        builder.extension(toolComponent, "removeRule", function -> function.param("rule", toolRule).returns(Void.TYPE).invoke(arguments -> PaperInventoryReflection.invoke(removeRule, arguments[0], arguments[1])));
        registerRule(builder, toolRule, tag);
    }
    private static void registerRule(JavaTypes.Builder builder, Class<?> toolRule, Class<?> tag) {
        Method getBlocks = PaperInventoryReflection.method(toolRule, "getBlocks");
        Method setMaterialBlocks = PaperInventoryReflection.method(toolRule, "setBlocks", Material.class);
        Method setCollectionBlocks = PaperInventoryReflection.method(toolRule, "setBlocks", Collection.class);
        Method setTagBlocks = PaperInventoryReflection.method(toolRule, "setBlocks", tag);
        Method getSpeed = PaperInventoryReflection.method(toolRule, "getSpeed");
        Method setSpeed = PaperInventoryReflection.method(toolRule, "setSpeed", Float.class);
        Method isCorrectForDrops = PaperInventoryReflection.method(toolRule, "isCorrectForDrops");
        Method setCorrectForDrops = PaperInventoryReflection.method(toolRule, "setCorrectForDrops", Boolean.class);
        JavaTypeRef tagType = JavaTypeRef.javaType(tag);
        builder.extension(toolRule, "blocks", function -> function.returns(Collection.class).invoke(arguments -> PaperInventoryReflection.invoke(getBlocks, arguments[0])));
        builder.extension(toolRule, "setBlocks", function -> function.param("material", Material.class).returns(Void.TYPE).invoke(arguments -> PaperInventoryReflection.invoke(setMaterialBlocks, arguments[0], arguments[1])));
        builder.extension(toolRule, "setBlocks", function -> function.param("materials", Collection.class).returns(Void.TYPE).invoke(arguments -> PaperInventoryReflection.invoke(setCollectionBlocks, arguments[0], arguments[1])));
        builder.extension(toolRule, "setBlocks", function -> function.param("tag", tagType).returns(Void.TYPE).invoke(arguments -> PaperInventoryReflection.invoke(setTagBlocks, arguments[0], arguments[1])));
        builder.extension(toolRule, "speed", function -> function.returns(Float.class).invoke(arguments -> PaperInventoryReflection.invoke(getSpeed, arguments[0])));
        builder.extension(toolRule, "setSpeed", function -> function.param("speed", Float.class).returns(Void.TYPE).invoke(arguments -> PaperInventoryReflection.invoke(setSpeed, arguments[0], arguments[1])));
        builder.extension(toolRule, "isCorrectForDrops", function -> function.returns(Boolean.class).invoke(arguments -> PaperInventoryReflection.invoke(isCorrectForDrops, arguments[0])));
        builder.extension(toolRule, "setCorrectForDrops", function -> function.param("correctForDrops", Boolean.class).returns(Void.TYPE).invoke(arguments -> PaperInventoryReflection.invoke(setCorrectForDrops, arguments[0], arguments[1])));
    }
}
