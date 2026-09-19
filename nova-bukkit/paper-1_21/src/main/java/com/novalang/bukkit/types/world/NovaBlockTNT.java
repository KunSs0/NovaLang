package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ TNT BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.TNT"}, methods = {
        "org.bukkit.block.data.type.TNT#isUnstable", "org.bukkit.block.data.type.TNT#setUnstable"})
public final class NovaBlockTNT {
    private static final String TNT = "org.bukkit.block.data.type.TNT";
    private NovaBlockTNT() {
    }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockTNT.class, TNT);
        Method isUnstable = NovaBlockDataReflection.method(type, "isUnstable");
        Method setUnstable = NovaBlockDataReflection.method(type, "setUnstable", Boolean.TYPE);
        builder.extension(type, "isUnstable", function -> function.returns(Boolean.class).invoke(arguments -> NovaBlockDataReflection.invoke(isUnstable, arguments[0])));
        builder.extension(type, "setUnstable", function -> function.param("unstable", Boolean.class).returns(Void.TYPE).invoke(arguments -> NovaBlockDataReflection.invoke(setUnstable, arguments[0], arguments[1])));
    }
}
