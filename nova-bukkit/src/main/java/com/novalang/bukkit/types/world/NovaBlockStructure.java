package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** Structure 方块状态的 Fluxon getter 别名与字符串枚举重载。 */
@Requires(classes = {
        "org.bukkit.block.Structure",
        "org.bukkit.block.structure.Mirror",
        "org.bukkit.block.structure.StructureRotation",
        "org.bukkit.block.structure.UsageMode",
        "org.bukkit.util.BlockVector"}, methods = {
        "org.bukkit.block.Structure#getStructureName",
        "org.bukkit.block.Structure#getAuthor",
        "org.bukkit.block.Structure#getRelativePosition",
        "org.bukkit.block.Structure#getStructureSize",
        "org.bukkit.block.Structure#getMirror",
        "org.bukkit.block.Structure#setMirror",
        "org.bukkit.block.Structure#getRotation",
        "org.bukkit.block.Structure#setRotation",
        "org.bukkit.block.Structure#getUsageMode",
        "org.bukkit.block.Structure#setUsageMode",
        "org.bukkit.block.Structure#getIntegrity",
        "org.bukkit.block.Structure#getSeed"})
public final class NovaBlockStructure {

    private static final String STRUCTURE = "org.bukkit.block.Structure";
    private static final String MIRROR = "org.bukkit.block.structure.Mirror";
    private static final String STRUCTURE_ROTATION = "org.bukkit.block.structure.StructureRotation";
    private static final String USAGE_MODE = "org.bukkit.block.structure.UsageMode";
    private static final String BLOCK_VECTOR = "org.bukkit.util.BlockVector";

    private NovaBlockStructure() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> structureType = NovaBlockDataReflection.type(NovaBlockStructure.class, STRUCTURE);
        Class<?> mirrorType = NovaBlockDataReflection.type(NovaBlockStructure.class, MIRROR);
        Class<?> rotationType = NovaBlockDataReflection.type(NovaBlockStructure.class, STRUCTURE_ROTATION);
        Class<?> usageModeType = NovaBlockDataReflection.type(NovaBlockStructure.class, USAGE_MODE);
        Class<?> blockVectorType = NovaBlockDataReflection.type(NovaBlockStructure.class, BLOCK_VECTOR);
        Method getStructureName = NovaBlockDataReflection.method(structureType, "getStructureName");
        Method getAuthor = NovaBlockDataReflection.method(structureType, "getAuthor");
        Method getRelativePosition = NovaBlockDataReflection.method(structureType, "getRelativePosition");
        Method getStructureSize = NovaBlockDataReflection.method(structureType, "getStructureSize");
        Method getMirror = NovaBlockDataReflection.method(structureType, "getMirror");
        Method setMirror = NovaBlockDataReflection.method(structureType, "setMirror", mirrorType);
        Method getRotation = NovaBlockDataReflection.method(structureType, "getRotation");
        Method setRotation = NovaBlockDataReflection.method(structureType, "setRotation", rotationType);
        Method getUsageMode = NovaBlockDataReflection.method(structureType, "getUsageMode");
        Method setUsageMode = NovaBlockDataReflection.method(structureType, "setUsageMode", usageModeType);
        Method getIntegrity = NovaBlockDataReflection.method(structureType, "getIntegrity");
        Method getSeed = NovaBlockDataReflection.method(structureType, "getSeed");

        builder.extension(structureType, "structureName", function -> function
                .returns(JavaTypeRef.javaType(String.class).nullable())
                .invoke(arguments -> NovaBlockDataReflection.invoke(getStructureName, arguments[0])));
        builder.extension(structureType, "author", function -> function
                .returns(JavaTypeRef.javaType(String.class).nullable())
                .invoke(arguments -> NovaBlockDataReflection.invoke(getAuthor, arguments[0])));
        builder.extension(structureType, "relativePosition", function -> function
                .returns(JavaTypeRef.javaType(blockVectorType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getRelativePosition, arguments[0])));
        builder.extension(structureType, "structureSize", function -> function
                .returns(JavaTypeRef.javaType(blockVectorType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getStructureSize, arguments[0])));
        builder.extension(structureType, "mirror", function -> function
                .returns(JavaTypeRef.javaType(mirrorType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getMirror, arguments[0])));
        builder.extension(structureType, "setMirror", function -> function
                .param("mirror", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> setEnum(setMirror, mirrorType, arguments)));
        builder.extension(structureType, "rotation", function -> function
                .returns(JavaTypeRef.javaType(rotationType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getRotation, arguments[0])));
        builder.extension(structureType, "setRotation", function -> function
                .param("rotation", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> setEnum(setRotation, rotationType, arguments)));
        builder.extension(structureType, "usageMode", function -> function
                .returns(JavaTypeRef.javaType(usageModeType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getUsageMode, arguments[0])));
        builder.extension(structureType, "setUsageMode", function -> function
                .param("usageMode", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> setEnum(setUsageMode, usageModeType, arguments)));
        builder.extension(structureType, "integrity", function -> function
                .returns(Float.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getIntegrity, arguments[0])));
        builder.extension(structureType, "seed", function -> function
                .returns(Long.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getSeed, arguments[0])));
    }

    private static Object setEnum(Method method, Class<?> enumType, Object[] arguments) {
        Object value = NovaBlockDataReflection.enumValue(enumType, (String) arguments[1]);
        if (value != null) {
            NovaBlockDataReflection.invoke(method, arguments[0], value);
        }
        return null;
    }
}
