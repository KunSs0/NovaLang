package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import java.lang.reflect.Method;

/** 1.15+ Bee 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Bee"}, methods = {"org.bukkit.entity.Bee#getHive", "org.bukkit.entity.Bee#setHive", "org.bukkit.entity.Bee#getFlower", "org.bukkit.entity.Bee#setFlower", "org.bukkit.entity.Bee#hasNectar", "org.bukkit.entity.Bee#setHasNectar", "org.bukkit.entity.Bee#hasStung", "org.bukkit.entity.Bee#setHasStung", "org.bukkit.entity.Bee#getAnger", "org.bukkit.entity.Bee#setAnger", "org.bukkit.entity.Bee#getCannotEnterHiveTicks", "org.bukkit.entity.Bee#setCannotEnterHiveTicks"})
public final class NovaBee {
    private static final String TYPE = "org.bukkit.entity.Bee";
    private NovaBee() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t = NovaEntityReflection.type(NovaBee.class, TYPE);
        Method hive=NovaEntityReflection.method(t,"getHive"),setHive=NovaEntityReflection.method(t,"setHive",Location.class),flower=NovaEntityReflection.method(t,"getFlower"),setFlower=NovaEntityReflection.method(t,"setFlower",Location.class),nectar=NovaEntityReflection.method(t,"hasNectar"),setNectar=NovaEntityReflection.method(t,"setHasNectar",Boolean.TYPE),stung=NovaEntityReflection.method(t,"hasStung"),setStung=NovaEntityReflection.method(t,"setHasStung",Boolean.TYPE),anger=NovaEntityReflection.method(t,"getAnger"),setAnger=NovaEntityReflection.method(t,"setAnger",Integer.TYPE),ticks=NovaEntityReflection.method(t,"getCannotEnterHiveTicks"),setTicks=NovaEntityReflection.method(t,"setCannotEnterHiveTicks",Integer.TYPE);
        JavaTypeRef location=JavaTypeRef.javaType(Location.class).nullable();
        b.extension(t,"hive",f->f.returns(location).invoke(a->NovaEntityReflection.invoke(hive,a[0]))); b.extension(t,"setHive",f->f.param("location",location).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setHive,a[0],a[1])));
        b.extension(t,"flower",f->f.returns(location).invoke(a->NovaEntityReflection.invoke(flower,a[0]))); b.extension(t,"setFlower",f->f.param("location",location).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setFlower,a[0],a[1])));
        b.extension(t,"hasNectar",f->f.returns(Boolean.class).invoke(a->NovaEntityReflection.invoke(nectar,a[0]))); b.extension(t,"setHasNectar",f->f.param("value",Boolean.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setNectar,a[0],a[1])));
        b.extension(t,"hasStung",f->f.returns(Boolean.class).invoke(a->NovaEntityReflection.invoke(stung,a[0]))); b.extension(t,"setHasStung",f->f.param("value",Boolean.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setStung,a[0],a[1])));
        b.extension(t,"anger",f->f.returns(Integer.class).invoke(a->NovaEntityReflection.invoke(anger,a[0]))); b.extension(t,"setAnger",f->f.param("anger",Integer.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setAnger,a[0],a[1])));
        b.extension(t,"cannotEnterHiveTicks",f->f.returns(Integer.class).invoke(a->NovaEntityReflection.invoke(ticks,a[0]))); b.extension(t,"setCannotEnterHiveTicks",f->f.param("ticks",Integer.class).returns(Void.TYPE).invoke(a->NovaEntityReflection.invoke(setTicks,a[0],a[1])));
    }
}
