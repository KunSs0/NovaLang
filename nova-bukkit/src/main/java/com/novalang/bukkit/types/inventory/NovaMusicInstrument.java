package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.NamespacedKey;
import java.lang.reflect.Method;

/** 1.20.5+ MusicInstrument 的 Fluxon 静态函数契约。 */
@Requires(classes={"org.bukkit.MusicInstrument"},methods={"org.bukkit.MusicInstrument#getByKey","org.bukkit.MusicInstrument#values"})
public final class NovaMusicInstrument {
    private static final String TYPE="org.bukkit.MusicInstrument";
    private NovaMusicInstrument() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaInventoryReflection.type(NovaMusicInstrument.class,TYPE); Method key=NovaInventoryReflection.method(t,"getByKey",NamespacedKey.class),values=NovaInventoryReflection.method(t,"values");
        b.extension(t,"getByKey",f->f.param("key",NamespacedKey.class).returns(JavaTypeRef.javaType(t).nullable()).invoke(a->NovaInventoryReflection.invoke(key,null,a[1])));
        b.extension(t,"values",f->f.returns(java.lang.reflect.Array.newInstance(t,0).getClass()).invoke(a->NovaInventoryReflection.invoke(values,null)));
    }
}
