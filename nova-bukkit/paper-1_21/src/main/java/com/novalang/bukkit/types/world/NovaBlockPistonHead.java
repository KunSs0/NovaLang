package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.13+ PistonHead BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.PistonHead"}, methods = {"org.bukkit.block.data.type.PistonHead#isShort", "org.bukkit.block.data.type.PistonHead#setShort"})
public final class NovaBlockPistonHead {
    private NovaBlockPistonHead() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockPistonHead.class, "org.bukkit.block.data.type.PistonHead");
        Method isShort = NovaBlockDataReflection.method(type, "isShort"); Method setShort = NovaBlockDataReflection.method(type, "setShort", Boolean.TYPE);
        builder.extension(type, "isShort", f -> f.returns(Boolean.class).invoke(a -> NovaBlockDataReflection.invoke(isShort, a[0])));
        builder.extension(type, "setShort", f -> f.param("short", Boolean.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setShort, a[0], a[1])));
    }
}
