package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import org.bukkit.Location;

/** 1.15+ Bee 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Bee"}, methods = {"org.bukkit.entity.Bee#getHive", "org.bukkit.entity.Bee#setHive", "org.bukkit.entity.Bee#getFlower", "org.bukkit.entity.Bee#setFlower", "org.bukkit.entity.Bee#hasNectar", "org.bukkit.entity.Bee#setHasNectar", "org.bukkit.entity.Bee#hasStung", "org.bukkit.entity.Bee#setHasStung", "org.bukkit.entity.Bee#getAnger", "org.bukkit.entity.Bee#setAnger", "org.bukkit.entity.Bee#getCannotEnterHiveTicks", "org.bukkit.entity.Bee#setCannotEnterHiveTicks"})
public final class NovaBee {
    private static final String TYPE = "org.bukkit.entity.Bee";
    private NovaBee() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaBee.class, TYPE); Method hive = PaperEntityReflection.method(type, "getHive"); Method setHive = PaperEntityReflection.method(type, "setHive", Location.class); Method flower = PaperEntityReflection.method(type, "getFlower"); Method setFlower = PaperEntityReflection.method(type, "setFlower", Location.class); Method nectar = PaperEntityReflection.method(type, "hasNectar"); Method setNectar = PaperEntityReflection.method(type, "setHasNectar", Boolean.TYPE); Method stung = PaperEntityReflection.method(type, "hasStung"); Method setStung = PaperEntityReflection.method(type, "setHasStung", Boolean.TYPE); Method anger = PaperEntityReflection.method(type, "getAnger"); Method setAnger = PaperEntityReflection.method(type, "setAnger", Integer.TYPE); Method ticks = PaperEntityReflection.method(type, "getCannotEnterHiveTicks"); Method setTicks = PaperEntityReflection.method(type, "setCannotEnterHiveTicks", Integer.TYPE); JavaTypeRef location = JavaTypeRef.javaType(Location.class).nullable();
        builder.extension(type, "hive", f -> f.returns(location).invoke(a -> PaperEntityReflection.invoke(hive, a[0]))); builder.extension(type, "setHive", f -> f.param("location", location).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setHive, a[0], a[1]))); builder.extension(type, "flower", f -> f.returns(location).invoke(a -> PaperEntityReflection.invoke(flower, a[0]))); builder.extension(type, "setFlower", f -> f.param("location", location).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setFlower, a[0], a[1]))); builder.extension(type, "hasNectar", f -> f.returns(Boolean.class).invoke(a -> PaperEntityReflection.invoke(nectar, a[0]))); builder.extension(type, "setHasNectar", f -> f.param("value", Boolean.class).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setNectar, a[0], a[1]))); builder.extension(type, "hasStung", f -> f.returns(Boolean.class).invoke(a -> PaperEntityReflection.invoke(stung, a[0]))); builder.extension(type, "setHasStung", f -> f.param("value", Boolean.class).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setStung, a[0], a[1]))); builder.extension(type, "anger", f -> f.returns(Integer.class).invoke(a -> PaperEntityReflection.invoke(anger, a[0]))); builder.extension(type, "setAnger", f -> f.param("anger", Integer.class).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setAnger, a[0], a[1]))); builder.extension(type, "cannotEnterHiveTicks", f -> f.returns(Integer.class).invoke(a -> PaperEntityReflection.invoke(ticks, a[0]))); builder.extension(type, "setCannotEnterHiveTicks", f -> f.param("ticks", Integer.class).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setTicks, a[0], a[1])));
    }
}
