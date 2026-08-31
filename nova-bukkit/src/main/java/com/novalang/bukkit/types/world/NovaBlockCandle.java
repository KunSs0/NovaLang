package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.17+ Candle BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Candle"}, methods = {
        "org.bukkit.block.data.type.Candle#getCandles", "org.bukkit.block.data.type.Candle#setCandles", "org.bukkit.block.data.type.Candle#getMaximumCandles"})
public final class NovaBlockCandle {
    private static final String CANDLE = "org.bukkit.block.data.type.Candle";
    private NovaBlockCandle() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockCandle.class, CANDLE);
        Method getCandles = NovaBlockDataReflection.method(type, "getCandles");
        Method setCandles = NovaBlockDataReflection.method(type, "setCandles", Integer.TYPE);
        Method getMaximumCandles = NovaBlockDataReflection.method(type, "getMaximumCandles");
        builder.extension(type, "candles", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getCandles, arguments[0])));
        builder.extension(type, "setCandles", function -> function.param("candles", Integer.class).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setCandles, arguments[0], arguments[1])));
        builder.extension(type, "maximumCandles", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getMaximumCandles, arguments[0])));
    }
}
