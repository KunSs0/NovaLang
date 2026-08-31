package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Stairs BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.type.Stairs", "org.bukkit.block.data.type.Stairs$Shape"},
        methods = {
                "org.bukkit.block.data.type.Stairs#getShape",
                "org.bukkit.block.data.type.Stairs#setShape"
        })
public final class NovaBlockStairs {

    private static final String STAIRS = "org.bukkit.block.data.type.Stairs";
    private static final String SHAPE = "org.bukkit.block.data.type.Stairs$Shape";

    private NovaBlockStairs() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> stairsType = NovaBlockDataReflection.type(NovaBlockStairs.class, STAIRS);
        Class<?> shapeType = NovaBlockDataReflection.type(NovaBlockStairs.class, SHAPE);
        Method getShape = NovaBlockDataReflection.method(stairsType, "getShape");
        Method setShape = NovaBlockDataReflection.method(stairsType, "setShape", shapeType);
        builder.extension(stairsType, "shape", function -> function.returns(JavaTypeRef.javaType(shapeType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getShape, arguments[0])));
        builder.extension(stairsType, "setShape", function -> function
                .param("shape", shapeType).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setShape, arguments[0], arguments[1])));
        builder.extension(stairsType, "setShape", function -> function
                .param("shape", String.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    Object shape = NovaBlockDataReflection.enumValue(shapeType, (String) arguments[1]);
                    if (shape != null) {
                        NovaBlockDataReflection.invoke(setShape, arguments[0], shape);
                    }
                    return null;
                }));
    }
}
