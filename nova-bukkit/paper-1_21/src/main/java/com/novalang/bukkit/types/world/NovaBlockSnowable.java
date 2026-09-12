package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Snowable BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Snowable"},
        methods = {"org.bukkit.block.data.Snowable#isSnowy", "org.bukkit.block.data.Snowable#setSnowy"})
public final class NovaBlockSnowable {

    private static final String SNOWABLE = "org.bukkit.block.data.Snowable";

    private NovaBlockSnowable() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockSnowable.class, SNOWABLE);
        Method isSnowy = NovaBlockDataReflection.method(type, "isSnowy");
        Method setSnowy = NovaBlockDataReflection.method(type, "setSnowy", Boolean.TYPE);
        builder.extension(type, "isSnowy", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(isSnowy, arguments[0])));
        builder.extension(type, "setSnowy", function -> function.param("snowy", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setSnowy, arguments[0], arguments[1])));
    }
}
