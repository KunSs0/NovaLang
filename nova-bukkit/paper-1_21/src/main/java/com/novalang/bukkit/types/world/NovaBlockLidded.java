package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** Lidded 方块状态的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.Lidded"}, methods = {
        "org.bukkit.block.Lidded#open",
        "org.bukkit.block.Lidded#close"})
public final class NovaBlockLidded {

    private static final String LIDDED = "org.bukkit.block.Lidded";

    private NovaBlockLidded() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> liddedType = NovaBlockDataReflection.type(NovaBlockLidded.class, LIDDED);
        Method open = NovaBlockDataReflection.method(liddedType, "open");
        Method close = NovaBlockDataReflection.method(liddedType, "close");

        builder.extension(liddedType, "open", function -> function
                .returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(open, arguments[0])));
        builder.extension(liddedType, "close", function -> function
                .returns(Void.TYPE)
                .invoke(arguments -> NovaBlockDataReflection.invoke(close, arguments[0])));
    }
}
