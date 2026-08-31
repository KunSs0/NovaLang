package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Slab BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.type.Slab", "org.bukkit.block.data.type.Slab$Type"},
        methods = {
                "org.bukkit.block.data.type.Slab#getType",
                "org.bukkit.block.data.type.Slab#setType"
        })
public final class NovaBlockSlab {

    private static final String SLAB = "org.bukkit.block.data.type.Slab";
    private static final String TYPE = "org.bukkit.block.data.type.Slab$Type";

    private NovaBlockSlab() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> slabType = NovaBlockDataReflection.type(NovaBlockSlab.class, SLAB);
        Class<?> typeType = NovaBlockDataReflection.type(NovaBlockSlab.class, TYPE);
        Method getType = NovaBlockDataReflection.method(slabType, "getType");
        Method setType = NovaBlockDataReflection.method(slabType, "setType", typeType);
        builder.extension(slabType, "type", function -> function.returns(JavaTypeRef.javaType(typeType))
                .invoke(arguments -> NovaBlockDataReflection.invoke(getType, arguments[0])));
        builder.extension(slabType, "setType", function -> function
                .param("type", typeType).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setType, arguments[0], arguments[1])));
        builder.extension(slabType, "setType", function -> function
                .param("type", String.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    Object type = NovaBlockDataReflection.enumValue(typeType, (String) arguments[1]);
                    if (type != null) {
                        NovaBlockDataReflection.invoke(setType, arguments[0], type);
                    }
                    return null;
                }));
    }
}
