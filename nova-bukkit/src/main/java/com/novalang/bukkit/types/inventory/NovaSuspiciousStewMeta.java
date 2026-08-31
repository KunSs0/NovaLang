package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.lang.reflect.Method;

/** 1.14+ SuspiciousStewMeta 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.inventory.meta.SuspiciousStewMeta"},methods={"org.bukkit.inventory.meta.SuspiciousStewMeta#hasCustomEffects","org.bukkit.inventory.meta.SuspiciousStewMeta#getCustomEffects","org.bukkit.inventory.meta.SuspiciousStewMeta#addCustomEffect","org.bukkit.inventory.meta.SuspiciousStewMeta#removeCustomEffect","org.bukkit.inventory.meta.SuspiciousStewMeta#hasCustomEffect","org.bukkit.inventory.meta.SuspiciousStewMeta#clearCustomEffects","org.bukkit.inventory.meta.SuspiciousStewMeta#clone"})
public final class NovaSuspiciousStewMeta {
    private static final String TYPE="org.bukkit.inventory.meta.SuspiciousStewMeta";
    private NovaSuspiciousStewMeta() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaInventoryReflection.type(NovaSuspiciousStewMeta.class,TYPE); Method h=NovaInventoryReflection.method(t,"hasCustomEffects"),g=NovaInventoryReflection.method(t,"getCustomEffects"),a=NovaInventoryReflection.method(t,"addCustomEffect",PotionEffect.class,Boolean.TYPE),r=NovaInventoryReflection.method(t,"removeCustomEffect",PotionEffectType.class),he=NovaInventoryReflection.method(t,"hasCustomEffect",PotionEffectType.class),cl=NovaInventoryReflection.method(t,"clearCustomEffects"),c=NovaInventoryReflection.method(t,"clone"); JavaTypeRef effects=JavaTypeRef.listOf(JavaTypeRef.javaType(PotionEffect.class));
        b.extension(t,"hasCustomEffects",f->f.returns(Boolean.class).invoke(x->NovaInventoryReflection.invoke(h,x[0])));
        b.extension(t,"customEffects",f->f.returns(effects).invoke(x->NovaInventoryReflection.invoke(g,x[0])));
        b.extension(t,"addCustomEffect",f->f.param("effect",PotionEffect.class).param("overwrite",Boolean.class).returns(Boolean.class).invoke(x->NovaInventoryReflection.invoke(a,x[0],x[1],x[2])));
        b.extension(t,"removeCustomEffect",f->f.param("type",PotionEffectType.class).returns(Boolean.class).invoke(x->NovaInventoryReflection.invoke(r,x[0],x[1])));
        b.extension(t,"hasCustomEffect",f->f.param("type",PotionEffectType.class).returns(Boolean.class).invoke(x->NovaInventoryReflection.invoke(he,x[0],x[1])));
        b.extension(t,"clearCustomEffects",f->f.returns(Boolean.class).invoke(x->NovaInventoryReflection.invoke(cl,x[0])));
        b.extension(t,"clone",f->f.returns(JavaTypeRef.javaType(t)).invoke(x->NovaInventoryReflection.invoke(c,x[0])));
    }
}
