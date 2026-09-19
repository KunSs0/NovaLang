package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;
import java.util.Set;

/** 1.13+ Rail BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Rail", "org.bukkit.block.data.Rail$Shape"},
        methods = {
                "org.bukkit.block.data.Rail#getShape",
                "org.bukkit.block.data.Rail#setShape",
                "org.bukkit.block.data.Rail#getShapes"
        })
public final class NovaBlockRail {

    private static final String RAIL = "org.bukkit.block.data.Rail";
    private static final String SHAPE = "org.bukkit.block.data.Rail$Shape";

    private NovaBlockRail() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> railType = NovaBlockDataReflection.type(NovaBlockRail.class, RAIL);
        Class<?> shapeType = NovaBlockDataReflection.type(NovaBlockRail.class, SHAPE);
        Method getShape = NovaBlockDataReflection.method(railType, "getShape");
        Method setShape = NovaBlockDataReflection.method(railType, "setShape", shapeType);
        Method getShapes = NovaBlockDataReflection.method(railType, "getShapes");
        builder.extension(railType, "shape", function -> function.returns(JavaTypeRef.javaType(shapeType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getShape, arguments[0])));
        builder.extension(railType, "setShape", function -> function
                .param("shape", shapeType).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setShape, arguments[0], arguments[1])));
        builder.extension(railType, "setShape", function -> function
                .param("shape", String.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    Object shape = NovaBlockDataReflection.enumValue(shapeType, (String) arguments[1]);
                    if (shape != null) {
                        NovaBlockDataReflection.invoke(setShape, arguments[0], shape);
                    }
                    return null;
                }));
        builder.extension(railType, "shapes", function -> function.returns(Set.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getShapes, arguments[0])));
    }
}
