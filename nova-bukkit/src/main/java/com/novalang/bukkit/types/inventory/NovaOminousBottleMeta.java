package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.21+ OminousBottleMeta 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.inventory.meta.OminousBottleMeta"},methods={"org.bukkit.inventory.meta.OminousBottleMeta#hasAmplifier","org.bukkit.inventory.meta.OminousBottleMeta#getAmplifier","org.bukkit.inventory.meta.OminousBottleMeta#setAmplifier","org.bukkit.inventory.meta.OminousBottleMeta#clone"})
public final class NovaOminousBottleMeta {
    private static final String TYPE="org.bukkit.inventory.meta.OminousBottleMeta";
    private NovaOminousBottleMeta() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaInventoryReflection.type(NovaOminousBottleMeta.class,TYPE); Method h=NovaInventoryReflection.method(t,"hasAmplifier"),g=NovaInventoryReflection.method(t,"getAmplifier"),s=NovaInventoryReflection.method(t,"setAmplifier",Integer.TYPE),c=NovaInventoryReflection.method(t,"clone");
        b.extension(t,"hasAmplifier",f->f.returns(Boolean.class).invoke(a->NovaInventoryReflection.invoke(h,a[0])));
        b.extension(t,"amplifier",f->f.returns(Integer.class).invoke(a->NovaInventoryReflection.invoke(g,a[0])));
        b.extension(t,"setAmplifier",f->f.param("amplifier",Integer.class).returns(Void.TYPE).invoke(a->NovaInventoryReflection.invoke(s,a[0],a[1])));
        b.extension(t,"clone",f->f.returns(JavaTypeRef.javaType(t)).invoke(a->NovaInventoryReflection.invoke(c,a[0])));
    }
}
