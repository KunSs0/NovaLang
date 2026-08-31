package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.19.4+ Display.Brightness 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.entity.Display$Brightness"},
        methods = {
                "org.bukkit.entity.Display$Brightness#getBlockLight",
                "org.bukkit.entity.Display$Brightness#getSkyLight"
        })
public final class NovaDisplayBrightness {

    private static final String TYPE = "org.bukkit.entity.Display$Brightness";

    private NovaDisplayBrightness() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaDisplayBrightness.class, TYPE);
        Method blockLight = NovaEntityReflection.method(type, "getBlockLight");
        Method skyLight = NovaEntityReflection.method(type, "getSkyLight");
        Method toString = NovaEntityReflection.method(type, "toString");
        builder.extension(type, "blockLight", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaEntityReflection.invoke(blockLight, arguments[0])));
        builder.extension(type, "skyLight", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaEntityReflection.invoke(skyLight, arguments[0])));
        builder.extension(type, "toString", function -> function.returns(String.class)
                .invoke(arguments -> NovaEntityReflection.invoke(toString, arguments[0])));
    }
}
