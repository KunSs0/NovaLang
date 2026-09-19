package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.19.4+ Display.Brightness 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Display$Brightness"}, methods = {"org.bukkit.entity.Display$Brightness#getBlockLight", "org.bukkit.entity.Display$Brightness#getSkyLight"})
public final class NovaDisplayBrightness {
    private static final String TYPE = "org.bukkit.entity.Display$Brightness";
    private NovaDisplayBrightness() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaDisplayBrightness.class, TYPE);
        Method blockLight = PaperEntityReflection.method(type, "getBlockLight");
        Method skyLight = PaperEntityReflection.method(type, "getSkyLight");
        Method toString = PaperEntityReflection.method(type, "toString");
        builder.extension(type, "blockLight", function -> function.returns(Integer.class).invoke(arguments -> PaperEntityReflection.invoke(blockLight, arguments[0])));
        builder.extension(type, "skyLight", function -> function.returns(Integer.class).invoke(arguments -> PaperEntityReflection.invoke(skyLight, arguments[0])));
        builder.extension(type, "toString", function -> function.returns(String.class).invoke(arguments -> PaperEntityReflection.invoke(toString, arguments[0])));
    }
}
