package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.20.5+ AbstractWindCharge 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.AbstractWindCharge"}, methods = {"org.bukkit.entity.AbstractWindCharge#explode"})
public final class NovaAbstractWindCharge {
    private static final String TYPE = "org.bukkit.entity.AbstractWindCharge";
    private NovaAbstractWindCharge() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaAbstractWindCharge.class, TYPE);
        Method explode = NovaEntityReflection.method(type, "explode");
        builder.extension(type, "explode", function -> function.returns(Void.TYPE).invoke(arguments -> NovaEntityReflection.invoke(explode, arguments[0])));
    }
}
