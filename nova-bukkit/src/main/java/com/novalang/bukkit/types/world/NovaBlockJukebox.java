package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.13+ Jukebox BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Jukebox"}, methods = {"org.bukkit.block.data.type.Jukebox#hasRecord"})
public final class NovaBlockJukebox {
    private NovaBlockJukebox() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockJukebox.class, "org.bukkit.block.data.type.Jukebox");
        Method hasRecord = NovaBlockDataReflection.method(type, "hasRecord");
        builder.extension(type, "hasRecord", f -> f.returns(Boolean.class).invoke(a -> NovaBlockDataReflection.invoke(hasRecord, a[0])));
    }
}
