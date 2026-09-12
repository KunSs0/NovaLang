package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ AnaloguePowerable BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.AnaloguePowerable"},
        methods = {
                "org.bukkit.block.data.AnaloguePowerable#getPower",
                "org.bukkit.block.data.AnaloguePowerable#setPower",
                "org.bukkit.block.data.AnaloguePowerable#getMaximumPower"
        })
public final class NovaBlockAnaloguePowerable {

    private static final String ANALOGUE_POWERABLE = "org.bukkit.block.data.AnaloguePowerable";

    private NovaBlockAnaloguePowerable() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockAnaloguePowerable.class, ANALOGUE_POWERABLE);
        Method getPower = NovaBlockDataReflection.method(type, "getPower");
        Method setPower = NovaBlockDataReflection.method(type, "setPower", Integer.TYPE);
        Method getMaximumPower = NovaBlockDataReflection.method(type, "getMaximumPower");
        builder.extension(type, "power", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getPower, arguments[0])));
        builder.extension(type, "setPower", function -> function.param("power", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setPower, arguments[0], arguments[1])));
        builder.extension(type, "maximumPower", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getMaximumPower, arguments[0])));
    }
}
