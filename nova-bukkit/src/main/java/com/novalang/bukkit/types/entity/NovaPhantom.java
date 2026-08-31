package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.13+ Phantom 的 Fluxon 函数契约。 */
@Requires(
        classes = {"org.bukkit.entity.Phantom"},
        methods = {"org.bukkit.entity.Phantom#getSize", "org.bukkit.entity.Phantom#setSize"})
public final class NovaPhantom {

    private static final String TYPE = "org.bukkit.entity.Phantom";

    private NovaPhantom() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaPhantom.class, TYPE);
        Method size = NovaEntityReflection.method(type, "getSize");
        Method setSize = NovaEntityReflection.method(type, "setSize", Integer.TYPE);
        builder.extension(type, "size", function -> function.returns(Integer.class)
                .invoke(arguments -> NovaEntityReflection.invoke(size, arguments[0])));
        builder.extension(type, "setSize", function -> function.param("size", Integer.class).returns(Void.TYPE)
                .invoke(arguments -> NovaEntityReflection.invoke(setSize, arguments[0], arguments[1])));
    }
}
