package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Hatchable BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Hatchable"},
        methods = {
                "org.bukkit.block.data.Hatchable#getHatch",
                "org.bukkit.block.data.Hatchable#setHatch",
                "org.bukkit.block.data.Hatchable#getMaximumHatch"
        })
public final class NovaBlockHatchable {

    private static final String HATCHABLE = "org.bukkit.block.data.Hatchable";

    private NovaBlockHatchable() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockHatchable.class, HATCHABLE);
        Method getHatch = NovaBlockDataReflection.method(type, "getHatch");
        Method setHatch = NovaBlockDataReflection.method(type, "setHatch", Integer.TYPE);
        Method getMaximumHatch = NovaBlockDataReflection.method(type, "getMaximumHatch");
        builder.extension(type, "hatch", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getHatch, arguments[0])));
        builder.extension(type, "setHatch", function -> function.param("hatch", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setHatch, arguments[0], arguments[1])));
        builder.extension(type, "maximumHatch", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getMaximumHatch, arguments[0])));
    }
}
