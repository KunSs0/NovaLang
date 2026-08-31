package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.14+ Lectern BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Lectern"}, methods = {"org.bukkit.block.data.type.Lectern#hasBook"})
public final class NovaBlockLectern {
    private NovaBlockLectern() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockLectern.class, "org.bukkit.block.data.type.Lectern");
        Method hasBook = NovaBlockDataReflection.method(type, "hasBook");
        builder.extension(type, "hasBook", f -> f.returns(Boolean.class).invoke(a -> NovaBlockDataReflection.invoke(hasBook, a[0])));
    }
}
