package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Method;
import java.util.Collection;

/** 1.13+ Conduit 方块状态的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.Conduit", "org.bukkit.util.BoundingBox"}, methods = {
        "org.bukkit.block.Conduit#isActive",
        "org.bukkit.block.Conduit#isHunting",
        "org.bukkit.block.Conduit#getFrameBlocks",
        "org.bukkit.block.Conduit#getFrameBlockCount",
        "org.bukkit.block.Conduit#getRange",
        "org.bukkit.block.Conduit#setTarget",
        "org.bukkit.block.Conduit#getTarget",
        "org.bukkit.block.Conduit#hasTarget",
        "org.bukkit.block.Conduit#getHuntingArea"})
public final class NovaBlockConduit {

    private static final String CONDUIT = "org.bukkit.block.Conduit";
    private static final String BOUNDING_BOX = "org.bukkit.util.BoundingBox";

    private NovaBlockConduit() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> conduitType = NovaBlockDataReflection.type(NovaBlockConduit.class, CONDUIT);
        Class<?> boundingBoxType = NovaBlockDataReflection.type(NovaBlockConduit.class, BOUNDING_BOX);
        Method isActive = NovaBlockDataReflection.method(conduitType, "isActive");
        Method isHunting = NovaBlockDataReflection.method(conduitType, "isHunting");
        Method getFrameBlocks = NovaBlockDataReflection.method(conduitType, "getFrameBlocks");
        Method getFrameBlockCount = NovaBlockDataReflection.method(conduitType, "getFrameBlockCount");
        Method getRange = NovaBlockDataReflection.method(conduitType, "getRange");
        Method setTarget = NovaBlockDataReflection.method(conduitType, "setTarget", LivingEntity.class);
        Method getTarget = NovaBlockDataReflection.method(conduitType, "getTarget");
        Method hasTarget = NovaBlockDataReflection.method(conduitType, "hasTarget");
        Method getHuntingArea = NovaBlockDataReflection.method(conduitType, "getHuntingArea");

        builder.extension(conduitType, "isActive", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(isActive, arguments[0])));
        builder.extension(conduitType, "isHunting", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(isHunting, arguments[0])));
        builder.extension(conduitType, "frameBlocks", function -> function.returns(Collection.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getFrameBlocks, arguments[0])));
        builder.extension(conduitType, "frameBlockCount", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getFrameBlockCount, arguments[0])));
        builder.extension(conduitType, "range", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getRange, arguments[0])));
        builder.extension(conduitType, "setTarget", function -> function.param("target", JavaTypeRef.javaType(LivingEntity.class).nullable())
                .returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setTarget, arguments[0], arguments[1])));
        builder.extension(conduitType, "target", function -> function.returns(JavaTypeRef.javaType(LivingEntity.class).nullable())
                .invoke(arguments -> NovaBlockDataReflection.invoke(getTarget, arguments[0])));
        builder.extension(conduitType, "hasTarget", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(hasTarget, arguments[0])));
        builder.extension(conduitType, "huntingArea", function -> function.returns(JavaTypeRef.javaType(boundingBoxType).nullable())
                .invoke(arguments -> NovaBlockDataReflection.invoke(getHuntingArea, arguments[0])));
    }
}
