package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.13+ CommandBlock BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.CommandBlock"}, methods = {"org.bukkit.block.data.type.CommandBlock#isConditional", "org.bukkit.block.data.type.CommandBlock#setConditional"})
public final class NovaBlockCommandBlock {
    private NovaBlockCommandBlock() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockCommandBlock.class, "org.bukkit.block.data.type.CommandBlock");
        Method isConditional = NovaBlockDataReflection.method(type, "isConditional"); Method setConditional = NovaBlockDataReflection.method(type, "setConditional", Boolean.TYPE);
        builder.extension(type, "isConditional", f -> f.returns(Boolean.class).invoke(a -> NovaBlockDataReflection.invoke(isConditional, a[0])));
        builder.extension(type, "setConditional", f -> f.param("conditional", Boolean.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setConditional, a[0], a[1])));
    }
}
