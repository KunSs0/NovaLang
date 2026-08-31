package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
/** 1.17+ CaveVinesPlant BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.CaveVinesPlant"}, methods = {"org.bukkit.block.data.type.CaveVinesPlant#isBerries", "org.bukkit.block.data.type.CaveVinesPlant#setBerries"})
public final class NovaBlockCaveVinesPlant {
    private NovaBlockCaveVinesPlant() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockCaveVinesPlant.class, "org.bukkit.block.data.type.CaveVinesPlant");
        Method isBerries = NovaBlockDataReflection.method(type, "isBerries"); Method setBerries = NovaBlockDataReflection.method(type, "setBerries", Boolean.TYPE);
        builder.extension(type, "isBerries", f -> f.returns(Boolean.class).invoke(a -> NovaBlockDataReflection.invoke(isBerries, a[0])));
        builder.extension(type, "setBerries", f -> f.param("berries", Boolean.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setBerries, a[0], a[1])));
    }
}
