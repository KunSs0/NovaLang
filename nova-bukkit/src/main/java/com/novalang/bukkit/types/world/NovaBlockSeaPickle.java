package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ SeaPickle BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.SeaPickle"}, methods = {
        "org.bukkit.block.data.type.SeaPickle#getPickles", "org.bukkit.block.data.type.SeaPickle#setPickles", "org.bukkit.block.data.type.SeaPickle#getMinimumPickles", "org.bukkit.block.data.type.SeaPickle#getMaximumPickles"})
public final class NovaBlockSeaPickle {
    private static final String SEA_PICKLE = "org.bukkit.block.data.type.SeaPickle";
    private NovaBlockSeaPickle() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockSeaPickle.class, SEA_PICKLE);
        Method getPickles = NovaBlockDataReflection.method(type, "getPickles");
        Method setPickles = NovaBlockDataReflection.method(type, "setPickles", Integer.TYPE);
        Method getMinimumPickles = NovaBlockDataReflection.method(type, "getMinimumPickles");
        Method getMaximumPickles = NovaBlockDataReflection.method(type, "getMaximumPickles");
        builder.extension(type, "pickles", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getPickles, arguments[0])));
        builder.extension(type, "setPickles", function -> function.param("pickles", Integer.class).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setPickles, arguments[0], arguments[1])));
        builder.extension(type, "minimumPickles", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getMinimumPickles, arguments[0])));
        builder.extension(type, "maximumPickles", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getMaximumPickles, arguments[0])));
    }
}
