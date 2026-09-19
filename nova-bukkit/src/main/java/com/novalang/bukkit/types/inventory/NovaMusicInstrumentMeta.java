package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.20.5+ MusicInstrumentMeta 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.inventory.meta.MusicInstrumentMeta","org.bukkit.MusicInstrument"},methods={"org.bukkit.inventory.meta.MusicInstrumentMeta#setInstrument","org.bukkit.inventory.meta.MusicInstrumentMeta#getInstrument","org.bukkit.inventory.meta.MusicInstrumentMeta#clone"})
public final class NovaMusicInstrumentMeta {
    private static final String TYPE="org.bukkit.inventory.meta.MusicInstrumentMeta", INSTRUMENT="org.bukkit.MusicInstrument";
    private NovaMusicInstrumentMeta() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaInventoryReflection.type(NovaMusicInstrumentMeta.class,TYPE),i=NovaInventoryReflection.type(NovaMusicInstrumentMeta.class,INSTRUMENT); Method set=NovaInventoryReflection.method(t,"setInstrument",i),get=NovaInventoryReflection.method(t,"getInstrument"),clone=NovaInventoryReflection.method(t,"clone");
        b.extension(t,"setInstrument",f->f.param("instrument",i).returns(Void.TYPE).invoke(a->NovaInventoryReflection.invoke(set,a[0],a[1])));
        b.extension(t,"instrument",f->f.returns(JavaTypeRef.javaType(i).nullable()).invoke(a->NovaInventoryReflection.invoke(get,a[0])));
        b.extension(t,"clone",f->f.returns(JavaTypeRef.javaType(t)).invoke(a->NovaInventoryReflection.invoke(clone,a[0])));
    }
}
