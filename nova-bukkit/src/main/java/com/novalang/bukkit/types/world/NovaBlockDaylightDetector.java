package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ DaylightDetector BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.DaylightDetector"}, methods = {
        "org.bukkit.block.data.type.DaylightDetector#isInverted", "org.bukkit.block.data.type.DaylightDetector#setInverted"})
public final class NovaBlockDaylightDetector {
    private static final String DAYLIGHT_DETECTOR = "org.bukkit.block.data.type.DaylightDetector";
    private NovaBlockDaylightDetector() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockDaylightDetector.class, DAYLIGHT_DETECTOR);
        Method isInverted = NovaBlockDataReflection.method(type, "isInverted");
        Method setInverted = NovaBlockDataReflection.method(type, "setInverted", Boolean.TYPE);
        builder.extension(type, "isInverted", function -> function.returns(Boolean.class).invoke(arguments -> NovaBlockDataReflection.invoke(isInverted, arguments[0])));
        builder.extension(type, "setInverted", function -> function.param("inverted", Boolean.class).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setInverted, arguments[0], arguments[1])));
    }
}
