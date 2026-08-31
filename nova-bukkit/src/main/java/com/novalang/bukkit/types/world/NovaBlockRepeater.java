package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Repeater BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Repeater"}, methods = {
        "org.bukkit.block.data.type.Repeater#getDelay", "org.bukkit.block.data.type.Repeater#setDelay", "org.bukkit.block.data.type.Repeater#getMinimumDelay", "org.bukkit.block.data.type.Repeater#getMaximumDelay", "org.bukkit.block.data.type.Repeater#isLocked", "org.bukkit.block.data.type.Repeater#setLocked"})
public final class NovaBlockRepeater {
    private static final String REPEATER = "org.bukkit.block.data.type.Repeater";
    private NovaBlockRepeater() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockRepeater.class, REPEATER);
        Method getDelay = NovaBlockDataReflection.method(type, "getDelay");
        Method setDelay = NovaBlockDataReflection.method(type, "setDelay", Integer.TYPE);
        Method getMinimumDelay = NovaBlockDataReflection.method(type, "getMinimumDelay");
        Method getMaximumDelay = NovaBlockDataReflection.method(type, "getMaximumDelay");
        Method isLocked = NovaBlockDataReflection.method(type, "isLocked");
        Method setLocked = NovaBlockDataReflection.method(type, "setLocked", Boolean.TYPE);
        builder.extension(type, "delay", f -> f.returns(Integer.class).invoke(a -> NovaBlockDataReflection.invoke(getDelay, a[0])));
        builder.extension(type, "setDelay", f -> f.param("delay", Integer.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setDelay, a[0], a[1])));
        builder.extension(type, "minimumDelay", f -> f.returns(Integer.class).invoke(a -> NovaBlockDataReflection.invoke(getMinimumDelay, a[0])));
        builder.extension(type, "maximumDelay", f -> f.returns(Integer.class).invoke(a -> NovaBlockDataReflection.invoke(getMaximumDelay, a[0])));
        builder.extension(type, "isLocked", f -> f.returns(Boolean.class).invoke(a -> NovaBlockDataReflection.invoke(isLocked, a[0])));
        builder.extension(type, "setLocked", f -> f.param("locked", Boolean.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setLocked, a[0], a[1])));
    }
}
