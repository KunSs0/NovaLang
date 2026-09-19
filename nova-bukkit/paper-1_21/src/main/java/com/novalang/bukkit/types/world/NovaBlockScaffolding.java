package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.14+ Scaffolding BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Scaffolding"}, methods = {
        "org.bukkit.block.data.type.Scaffolding#isBottom", "org.bukkit.block.data.type.Scaffolding#setBottom", "org.bukkit.block.data.type.Scaffolding#getDistance", "org.bukkit.block.data.type.Scaffolding#setDistance", "org.bukkit.block.data.type.Scaffolding#getMaximumDistance"})
public final class NovaBlockScaffolding {
    private static final String SCAFFOLDING = "org.bukkit.block.data.type.Scaffolding";
    private NovaBlockScaffolding() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockScaffolding.class, SCAFFOLDING);
        Method isBottom = NovaBlockDataReflection.method(type, "isBottom");
        Method setBottom = NovaBlockDataReflection.method(type, "setBottom", Boolean.TYPE);
        Method getDistance = NovaBlockDataReflection.method(type, "getDistance");
        Method setDistance = NovaBlockDataReflection.method(type, "setDistance", Integer.TYPE);
        Method getMaximumDistance = NovaBlockDataReflection.method(type, "getMaximumDistance");
        builder.extension(type, "isBottom", function -> function.returns(Boolean.class).invoke(arguments -> NovaBlockDataReflection.invoke(isBottom, arguments[0])));
        builder.extension(type, "setBottom", function -> function.param("bottom", Boolean.class).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setBottom, arguments[0], arguments[1])));
        builder.extension(type, "distance", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getDistance, arguments[0])));
        builder.extension(type, "setDistance", function -> function.param("distance", Integer.class).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setDistance, arguments[0], arguments[1])));
        builder.extension(type, "maximumDistance", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getMaximumDistance, arguments[0])));
    }
}
