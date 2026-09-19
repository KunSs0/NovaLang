package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Door BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.type.Door", "org.bukkit.block.data.type.Door$Hinge"},
        methods = {
                "org.bukkit.block.data.type.Door#getHinge",
                "org.bukkit.block.data.type.Door#setHinge"
        })
public final class NovaBlockDoor {

    private static final String DOOR = "org.bukkit.block.data.type.Door";
    private static final String HINGE = "org.bukkit.block.data.type.Door$Hinge";

    private NovaBlockDoor() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> doorType = NovaBlockDataReflection.type(NovaBlockDoor.class, DOOR);
        Class<?> hingeType = NovaBlockDataReflection.type(NovaBlockDoor.class, HINGE);
        Method getHinge = NovaBlockDataReflection.method(doorType, "getHinge");
        Method setHinge = NovaBlockDataReflection.method(doorType, "setHinge", hingeType);
        builder.extension(doorType, "hinge", function -> function.returns(JavaTypeRef.javaType(hingeType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getHinge, arguments[0])));
        builder.extension(doorType, "setHinge", function -> function
                .param("hinge", hingeType).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setHinge, arguments[0], arguments[1])));
        builder.extension(doorType, "setHinge", function -> function
                .param("hinge", String.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    Object hinge = NovaBlockDataReflection.enumValue(hingeType, (String) arguments[1]);
                    if (hinge != null) {
                        NovaBlockDataReflection.invoke(setHinge, arguments[0], hinge);
                    }
                    return null;
                }));
    }
}
