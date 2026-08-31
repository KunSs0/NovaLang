package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.16+ Strider 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Strider"}, methods = {
        "org.bukkit.entity.Strider#isShivering", "org.bukkit.entity.Strider#setShivering"})
public final class NovaStrider {

    private static final String TYPE = "org.bukkit.entity.Strider";

    private NovaStrider() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaStrider.class, TYPE);
        Method shivering = NovaEntityReflection.method(type, "isShivering");
        Method setShivering = NovaEntityReflection.method(type, "setShivering", Boolean.TYPE);
        builder.extension(type, "isShivering", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaEntityReflection.invoke(shivering, arguments[0])));
        builder.extension(type, "setShivering", function -> function.param("shivering", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setShivering, arguments[0], arguments[1])));
    }
}
