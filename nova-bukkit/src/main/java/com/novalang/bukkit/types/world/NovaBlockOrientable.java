package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Orientable BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Orientable", "org.bukkit.Axis"},
        methods = {
                "org.bukkit.block.data.Orientable#getAxis",
                "org.bukkit.block.data.Orientable#setAxis",
                "org.bukkit.block.data.Orientable#getAxes"
        })
public final class NovaBlockOrientable {

    private static final String ORIENTABLE = "org.bukkit.block.data.Orientable";
    private static final String AXIS = "org.bukkit.Axis";

    private NovaBlockOrientable() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> orientableType = NovaBlockDataReflection.type(NovaBlockOrientable.class, ORIENTABLE);
        Class<?> axisType = NovaBlockDataReflection.type(NovaBlockOrientable.class, AXIS);
        Method getAxis = NovaBlockDataReflection.method(orientableType, "getAxis");
        Method setAxis = NovaBlockDataReflection.method(orientableType, "setAxis", axisType);
        Method getAxes = NovaBlockDataReflection.method(orientableType, "getAxes");
        builder.extension(orientableType, "axis", function -> function.returns(JavaTypeRef.javaType(axisType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getAxis, arguments[0])));
        builder.extension(orientableType, "setAxis", function -> function
                .param("axis", axisType).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setAxis, arguments[0], arguments[1])));
        builder.extension(orientableType, "setAxis", function -> function
                .param("axis", String.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    Object axis = NovaBlockDataReflection.enumValue(axisType, (String) arguments[1]);
                    if (axis != null) {
                        NovaBlockDataReflection.invoke(setAxis, arguments[0], axis);
                    }
                    return null;
                }));
        builder.extension(orientableType, "axes", function -> function
                .returns(JavaTypeRef.setOf(JavaTypeRef.javaType(axisType)))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getAxes, arguments[0])));
    }
}
