package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Cake BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Cake"}, methods = {
        "org.bukkit.block.data.type.Cake#getBites", "org.bukkit.block.data.type.Cake#setBites", "org.bukkit.block.data.type.Cake#getMaximumBites"})
public final class NovaBlockCake {
    private static final String CAKE = "org.bukkit.block.data.type.Cake";
    private NovaBlockCake() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockCake.class, CAKE);
        Method getBites = NovaBlockDataReflection.method(type, "getBites");
        Method setBites = NovaBlockDataReflection.method(type, "setBites", Integer.TYPE);
        Method getMaximumBites = NovaBlockDataReflection.method(type, "getMaximumBites");
        builder.extension(type, "bites", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getBites, arguments[0])));
        builder.extension(type, "setBites", function -> function.param("bites", Integer.class).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setBites, arguments[0], arguments[1])));
        builder.extension(type, "maximumBites", function -> function.returns(Integer.class).invoke(arguments -> NovaBlockDataReflection.invoke(getMaximumBites, arguments[0])));
    }
}
