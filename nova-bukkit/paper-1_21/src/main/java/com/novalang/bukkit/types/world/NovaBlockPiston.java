package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;

/** 1.13+ Piston BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Piston"}, methods = {
        "org.bukkit.block.data.type.Piston#isExtended", "org.bukkit.block.data.type.Piston#setExtended"})
public final class NovaBlockPiston {
    private static final String PISTON = "org.bukkit.block.data.type.Piston";
    private NovaBlockPiston() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockPiston.class, PISTON);
        Method isExtended = NovaBlockDataReflection.method(type, "isExtended");
        Method setExtended = NovaBlockDataReflection.method(type, "setExtended", Boolean.TYPE);
        builder.extension(type, "isExtended", f -> f.returns(Boolean.class).invoke(a -> NovaBlockDataReflection.invoke(isExtended, a[0])));
        builder.extension(type, "setExtended", f -> f.param("extended", Boolean.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setExtended, a[0], a[1])));
    }
}
