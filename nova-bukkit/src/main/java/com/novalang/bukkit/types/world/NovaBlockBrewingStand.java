package com.novalang.bukkit.types.world;
import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import java.util.Set;
/** 1.13+ BrewingStand BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.BrewingStand"}, methods = {"org.bukkit.block.data.type.BrewingStand#hasBottle", "org.bukkit.block.data.type.BrewingStand#setBottle", "org.bukkit.block.data.type.BrewingStand#getBottles", "org.bukkit.block.data.type.BrewingStand#getMaximumBottles"})
public final class NovaBlockBrewingStand {
    private NovaBlockBrewingStand() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockBrewingStand.class, "org.bukkit.block.data.type.BrewingStand");
        Method hasBottle = NovaBlockDataReflection.method(type, "hasBottle", Integer.TYPE); Method setBottle = NovaBlockDataReflection.method(type, "setBottle", Integer.TYPE, Boolean.TYPE);
        Method getBottles = NovaBlockDataReflection.method(type, "getBottles"); Method getMaximumBottles = NovaBlockDataReflection.method(type, "getMaximumBottles");
        builder.extension(type, "hasBottle", f -> f.param("bottle", Integer.class).returns(Boolean.class).invoke(a -> NovaBlockDataReflection.invoke(hasBottle, a[0], a[1])));
        builder.extension(type, "setBottle", f -> f.param("bottle", Integer.class).param("present", Boolean.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setBottle, a[0], a[1], a[2])));
        builder.extension(type, "bottles", f -> f.returns(Set.class).invoke(a -> NovaBlockDataReflection.invoke(getBottles, a[0])));
        builder.extension(type, "maximumBottles", f -> f.returns(Integer.class).invoke(a -> NovaBlockDataReflection.invoke(getMaximumBottles, a[0])));
    }
}
