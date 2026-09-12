package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.13+ Sapling BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Sapling"}, methods = {"org.bukkit.block.data.type.Sapling#getStage", "org.bukkit.block.data.type.Sapling#setStage", "org.bukkit.block.data.type.Sapling#getMaximumStage"})
public final class NovaBlockSapling {
    private NovaBlockSapling() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockSapling.class, "org.bukkit.block.data.type.Sapling");
        Method getStage = NovaBlockDataReflection.method(type, "getStage"); Method setStage = NovaBlockDataReflection.method(type, "setStage", Integer.TYPE); Method getMaximumStage = NovaBlockDataReflection.method(type, "getMaximumStage");
        builder.extension(type, "stage", f -> f.returns(Integer.class).invoke(a -> NovaBlockDataReflection.invoke(getStage, a[0])));
        builder.extension(type, "setStage", f -> f.param("stage", Integer.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setStage, a[0], a[1])));
        builder.extension(type, "maximumStage", f -> f.returns(Integer.class).invoke(a -> NovaBlockDataReflection.invoke(getMaximumStage, a[0])));
    }
}
