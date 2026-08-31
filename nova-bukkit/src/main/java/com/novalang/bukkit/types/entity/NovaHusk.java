package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.13+ Husk 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Husk"}, methods = {
        "org.bukkit.entity.Husk#isConverting", "org.bukkit.entity.Husk#getConversionTime", "org.bukkit.entity.Husk#setConversionTime"})
public final class NovaHusk {
    private static final String TYPE = "org.bukkit.entity.Husk";
    private NovaHusk() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaHusk.class, TYPE);
        Method converting = NovaEntityReflection.method(type, "isConverting");
        Method conversionTime = NovaEntityReflection.method(type, "getConversionTime");
        Method setConversionTime = NovaEntityReflection.method(type, "setConversionTime", Integer.TYPE);
        builder.extension(type, "isConverting", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaEntityReflection.invoke(converting, arguments[0])));
        builder.extension(type, "conversionTime", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaEntityReflection.invoke(conversionTime, arguments[0])));
        builder.extension(type, "setConversionTime", function -> function.param("ticks", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setConversionTime, arguments[0], arguments[1])));
    }
}
