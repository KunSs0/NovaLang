package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;

import java.lang.reflect.Method;

/** 1.16+ CompassMeta 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.inventory.meta.CompassMeta"}, methods = {
        "org.bukkit.inventory.meta.CompassMeta#hasLodestone",
        "org.bukkit.inventory.meta.CompassMeta#getLodestone",
        "org.bukkit.inventory.meta.CompassMeta#setLodestone",
        "org.bukkit.inventory.meta.CompassMeta#isLodestoneTracked",
        "org.bukkit.inventory.meta.CompassMeta#setLodestoneTracked",
        "org.bukkit.inventory.meta.CompassMeta#clone"})
public final class NovaCompassMeta {
    private static final String TYPE = "org.bukkit.inventory.meta.CompassMeta";
    private NovaCompassMeta() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaInventoryReflection.type(NovaCompassMeta.class, TYPE);
        Method has = NovaInventoryReflection.method(type, "hasLodestone");
        Method get = NovaInventoryReflection.method(type, "getLodestone");
        Method set = NovaInventoryReflection.method(type, "setLodestone", Location.class);
        Method tracked = NovaInventoryReflection.method(type, "isLodestoneTracked");
        Method setTracked = NovaInventoryReflection.method(type, "setLodestoneTracked", Boolean.TYPE);
        Method clone = NovaInventoryReflection.method(type, "clone");
        builder.extension(type, "hasLodestone", f -> f.returns(Boolean.class).invoke(a -> NovaInventoryReflection.invoke(has, a[0])));
        builder.extension(type, "lodestone", f -> f.returns(JavaTypeRef.javaType(Location.class).nullable()).invoke(a -> NovaInventoryReflection.invoke(get, a[0])));
        builder.extension(type, "setLodestone", f -> f.param("location", JavaTypeRef.javaType(Location.class).nullable()).returns(Void.TYPE).invoke(a -> NovaInventoryReflection.invoke(set, a[0], a[1])));
        builder.extension(type, "isLodestoneTracked", f -> f.returns(Boolean.class).invoke(a -> NovaInventoryReflection.invoke(tracked, a[0])));
        builder.extension(type, "setLodestoneTracked", f -> f.param("tracked", Boolean.class).returns(Void.TYPE).invoke(a -> NovaInventoryReflection.invoke(setTracked, a[0], a[1])));
        builder.extension(type, "clone", f -> f.returns(JavaTypeRef.javaType(type)).invoke(a -> NovaInventoryReflection.invoke(clone, a[0])));
    }
}
