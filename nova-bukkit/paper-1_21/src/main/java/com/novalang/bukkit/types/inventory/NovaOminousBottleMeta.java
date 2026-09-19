package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.inventory.PaperInventoryReflection;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.21+ OminousBottleMeta 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.inventory.meta.OminousBottleMeta"},methods={"org.bukkit.inventory.meta.OminousBottleMeta#hasAmplifier","org.bukkit.inventory.meta.OminousBottleMeta#getAmplifier","org.bukkit.inventory.meta.OminousBottleMeta#setAmplifier","org.bukkit.inventory.meta.OminousBottleMeta#clone"})
public final class NovaOminousBottleMeta {
    private static final String TYPE="org.bukkit.inventory.meta.OminousBottleMeta";
    private NovaOminousBottleMeta() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=PaperInventoryReflection.type(NovaOminousBottleMeta.class,TYPE); Method h=PaperInventoryReflection.method(t,"hasAmplifier"),g=PaperInventoryReflection.method(t,"getAmplifier"),s=PaperInventoryReflection.method(t,"setAmplifier",Integer.TYPE),c=PaperInventoryReflection.method(t,"clone");
        b.extension(t,"hasAmplifier",f->f.returns(Boolean.class).invoke(a->PaperInventoryReflection.invoke(h,a[0])));
        b.extension(t,"amplifier",f->f.returns(Integer.class).invoke(a->PaperInventoryReflection.invoke(g,a[0])));
        b.extension(t,"setAmplifier",f->f.param("amplifier",Integer.class).returns(Void.TYPE).invoke(a->PaperInventoryReflection.invoke(s,a[0],a[1])));
        b.extension(t,"clone",f->f.returns(JavaTypeRef.javaType(t)).invoke(a->PaperInventoryReflection.invoke(c,a[0])));
    }
}
