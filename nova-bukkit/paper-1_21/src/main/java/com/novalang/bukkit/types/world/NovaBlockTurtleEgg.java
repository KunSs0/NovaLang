package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ TurtleEgg BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.TurtleEgg"}, methods = {
        "org.bukkit.block.data.type.TurtleEgg#getEggs", "org.bukkit.block.data.type.TurtleEgg#setEggs", "org.bukkit.block.data.type.TurtleEgg#getMinimumEggs", "org.bukkit.block.data.type.TurtleEgg#getMaximumEggs"})
public final class NovaBlockTurtleEgg {
    private static final String TURTLE_EGG = "org.bukkit.block.data.type.TurtleEgg";
    private NovaBlockTurtleEgg() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockTurtleEgg.class, TURTLE_EGG);
        Method getEggs = NovaBlockDataReflection.method(type, "getEggs");
        Method setEggs = NovaBlockDataReflection.method(type, "setEggs", Integer.TYPE);
        Method getMinimumEggs = NovaBlockDataReflection.method(type, "getMinimumEggs");
        Method getMaximumEggs = NovaBlockDataReflection.method(type, "getMaximumEggs");
        builder.extension(type, "eggs", f -> f.returns(Integer.class).invoke(a -> NovaBlockDataReflection.invoke(getEggs, a[0])));
        builder.extension(type, "setEggs", f -> f.param("eggs", Integer.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setEggs, a[0], a[1])));
        builder.extension(type, "minimumEggs", f -> f.returns(Integer.class).invoke(a -> NovaBlockDataReflection.invoke(getMinimumEggs, a[0])));
        builder.extension(type, "maximumEggs", f -> f.returns(Integer.class).invoke(a -> NovaBlockDataReflection.invoke(getMaximumEggs, a[0])));
    }
}
