package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ StructureBlock BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {
        "org.bukkit.block.data.type.StructureBlock",
        "org.bukkit.block.data.type.StructureBlock$Mode"}, methods = {
        "org.bukkit.block.data.type.StructureBlock#getMode",
        "org.bukkit.block.data.type.StructureBlock#setMode"})
public final class NovaBlockStructureBlock {

    private static final String STRUCTURE_BLOCK = "org.bukkit.block.data.type.StructureBlock";
    private static final String MODE = "org.bukkit.block.data.type.StructureBlock$Mode";

    private NovaBlockStructureBlock() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> structureBlockType = NovaBlockDataReflection.type(NovaBlockStructureBlock.class, STRUCTURE_BLOCK);
        Class<?> modeType = NovaBlockDataReflection.type(NovaBlockStructureBlock.class, MODE);
        Method getMode = NovaBlockDataReflection.method(structureBlockType, "getMode");
        Method setMode = NovaBlockDataReflection.method(structureBlockType, "setMode", modeType);

        builder.extension(structureBlockType, "mode", function -> function
                .returns(JavaTypeRef.javaType(modeType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getMode, arguments[0])));
        builder.extension(structureBlockType, "setMode", function -> function
                .param("mode", modeType)
                .returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setMode, arguments[0], arguments[1])));
        builder.extension(structureBlockType, "setMode", function -> function
                .param("mode", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    Object mode = NovaBlockDataReflection.enumValue(modeType, (String) arguments[1]);
                    if (mode != null) {
                        NovaBlockDataReflection.invoke(setMode, arguments[0], mode);
                    }
                    return null;
                }));
    }
}
