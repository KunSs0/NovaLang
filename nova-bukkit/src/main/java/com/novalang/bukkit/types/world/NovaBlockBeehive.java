package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.15+ Beehive BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Beehive"}, methods = {
        "org.bukkit.block.data.type.Beehive#getHoneyLevel", "org.bukkit.block.data.type.Beehive#setHoneyLevel", "org.bukkit.block.data.type.Beehive#getMaximumHoneyLevel"})
public final class NovaBlockBeehive {
    private static final String BEEHIVE = "org.bukkit.block.data.type.Beehive";
    private NovaBlockBeehive() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockBeehive.class, BEEHIVE);
        Method getHoneyLevel = NovaBlockDataReflection.method(type, "getHoneyLevel");
        Method setHoneyLevel = NovaBlockDataReflection.method(type, "setHoneyLevel", Integer.TYPE);
        Method getMaximumHoneyLevel = NovaBlockDataReflection.method(type, "getMaximumHoneyLevel");
        builder.extension(type, "honeyLevel", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getHoneyLevel, arguments[0])));
        builder.extension(type, "setHoneyLevel", function -> function.param("honeyLevel", Integer.class).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setHoneyLevel, arguments[0], arguments[1])));
        builder.extension(type, "maximumHoneyLevel", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getMaximumHoneyLevel, arguments[0])));
    }
}
