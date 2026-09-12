package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Levelled BlockData 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.block.data.Levelled"},
        methods = {
                "org.bukkit.block.data.Levelled#getLevel",
                "org.bukkit.block.data.Levelled#setLevel",
                "org.bukkit.block.data.Levelled#getMaximumLevel"
        })
public final class NovaBlockLevelled {

    private static final String LEVELLED = "org.bukkit.block.data.Levelled";

    private NovaBlockLevelled() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> levelledType = NovaBlockDataReflection.type(NovaBlockLevelled.class, LEVELLED);
        Method getLevel = NovaBlockDataReflection.method(levelledType, "getLevel");
        Method setLevel = NovaBlockDataReflection.method(levelledType, "setLevel", Integer.TYPE);
        Method getMaximumLevel = NovaBlockDataReflection.method(levelledType, "getMaximumLevel");
        builder.extension(levelledType, "level", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getLevel, arguments[0])));
        builder.extension(levelledType, "setLevel", function -> function
                .param("level", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(setLevel, arguments[0], arguments[1])));
        builder.extension(levelledType, "maximumLevel", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaBlockDataReflection.invoke(getMaximumLevel, arguments[0])));
    }
}
