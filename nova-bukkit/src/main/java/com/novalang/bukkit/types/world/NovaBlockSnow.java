package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Snow BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Snow"}, methods = {
        "org.bukkit.block.data.type.Snow#getLayers", "org.bukkit.block.data.type.Snow#setLayers", "org.bukkit.block.data.type.Snow#getMinimumLayers", "org.bukkit.block.data.type.Snow#getMaximumLayers"})
public final class NovaBlockSnow {
    private static final String SNOW = "org.bukkit.block.data.type.Snow";
    private NovaBlockSnow() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockSnow.class, SNOW);
        Method getLayers = NovaBlockDataReflection.method(type, "getLayers");
        Method setLayers = NovaBlockDataReflection.method(type, "setLayers", Integer.TYPE);
        Method getMinimumLayers = NovaBlockDataReflection.method(type, "getMinimumLayers");
        Method getMaximumLayers = NovaBlockDataReflection.method(type, "getMaximumLayers");
        builder.extension(type, "layers", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getLayers, arguments[0])));
        builder.extension(type, "setLayers", function -> function.param("layers", Integer.class).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setLayers, arguments[0], arguments[1])));
        builder.extension(type, "minimumLayers", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getMinimumLayers, arguments[0])));
        builder.extension(type, "maximumLayers", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getMaximumLayers, arguments[0])));
    }
}
