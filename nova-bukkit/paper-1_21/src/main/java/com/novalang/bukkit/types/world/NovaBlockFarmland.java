package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Farmland BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Farmland"}, methods = {
        "org.bukkit.block.data.type.Farmland#getMoisture", "org.bukkit.block.data.type.Farmland#setMoisture", "org.bukkit.block.data.type.Farmland#getMaximumMoisture"})
public final class NovaBlockFarmland {
    private static final String FARMLAND = "org.bukkit.block.data.type.Farmland";
    private NovaBlockFarmland() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockFarmland.class, FARMLAND);
        Method getMoisture = NovaBlockDataReflection.method(type, "getMoisture");
        Method setMoisture = NovaBlockDataReflection.method(type, "setMoisture", Integer.TYPE);
        Method getMaximumMoisture = NovaBlockDataReflection.method(type, "getMaximumMoisture");
        builder.extension(type, "moisture", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getMoisture, arguments[0])));
        builder.extension(type, "setMoisture", function -> function.param("moisture", Integer.class).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setMoisture, arguments[0], arguments[1])));
        builder.extension(type, "maximumMoisture", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getMaximumMoisture, arguments[0])));
    }
}
