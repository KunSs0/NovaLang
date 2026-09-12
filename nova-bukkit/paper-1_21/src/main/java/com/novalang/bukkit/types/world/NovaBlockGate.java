package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Gate BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Gate"}, methods = {
        "org.bukkit.block.data.type.Gate#isInWall", "org.bukkit.block.data.type.Gate#setInWall"})
public final class NovaBlockGate {
    private static final String GATE = "org.bukkit.block.data.type.Gate";
    private NovaBlockGate() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockGate.class, GATE);
        Method isInWall = NovaBlockDataReflection.method(type, "isInWall");
        Method setInWall = NovaBlockDataReflection.method(type, "setInWall", Boolean.TYPE);
        builder.extension(type, "isInWall", f -> f.returns(Boolean.class).invoke(a -> NovaBlockDataReflection.invoke(isInWall, a[0])));
        builder.extension(type, "setInWall", f -> f.param("inWall", Boolean.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setInWall, a[0], a[1])));
    }
}
