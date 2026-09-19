package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.13+ Leaves BlockData 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.block.data.type.Leaves"}, methods = {"org.bukkit.block.data.type.Leaves#isPersistent", "org.bukkit.block.data.type.Leaves#setPersistent", "org.bukkit.block.data.type.Leaves#getDistance", "org.bukkit.block.data.type.Leaves#setDistance"})
public final class NovaBlockLeaves {
    private NovaBlockLeaves() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaBlockDataReflection.type(NovaBlockLeaves.class, "org.bukkit.block.data.type.Leaves");
        Method isPersistent = NovaBlockDataReflection.method(type, "isPersistent"); Method setPersistent = NovaBlockDataReflection.method(type, "setPersistent", Boolean.TYPE);
        Method getDistance = NovaBlockDataReflection.method(type, "getDistance"); Method setDistance = NovaBlockDataReflection.method(type, "setDistance", Integer.TYPE);
        builder.extension(type, "isPersistent", f -> f.returns(Boolean.class).invoke(a -> NovaBlockDataReflection.invoke(isPersistent, a[0])));
        builder.extension(type, "setPersistent", f -> f.param("persistent", Boolean.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setPersistent, a[0], a[1])));
        builder.extension(type, "distance", f -> f.returns(Integer.class).invoke(a -> NovaBlockDataReflection.invoke(getDistance, a[0])));
        builder.extension(type, "setDistance", f -> f.param("distance", Integer.class).returns(Void.TYPE).invoke(a -> NovaBlockDataReflection.invoke(setDistance, a[0], a[1])));
    }
}
