package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.13+ Damageable 的 Fluxon 函数契约。 */
@Requires(classes={"org.bukkit.inventory.meta.Damageable"},methods={"org.bukkit.inventory.meta.Damageable#hasDamage","org.bukkit.inventory.meta.Damageable#getDamage","org.bukkit.inventory.meta.Damageable#setDamage","org.bukkit.inventory.meta.Damageable#hasMaxDamage","org.bukkit.inventory.meta.Damageable#getMaxDamage","org.bukkit.inventory.meta.Damageable#setMaxDamage","org.bukkit.inventory.meta.Damageable#clone"})
public final class NovaDamageable {
    private static final String TYPE="org.bukkit.inventory.meta.Damageable";
    private NovaDamageable() { }
    public static void register(JavaTypes.Builder b) {
        Class<?> t=NovaInventoryReflection.type(NovaDamageable.class,TYPE); Method h=NovaInventoryReflection.method(t,"hasDamage"),g=NovaInventoryReflection.method(t,"getDamage"),s=NovaInventoryReflection.method(t,"setDamage",Integer.TYPE),hm=NovaInventoryReflection.method(t,"hasMaxDamage"),gm=NovaInventoryReflection.method(t,"getMaxDamage"),sm=NovaInventoryReflection.method(t,"setMaxDamage",Integer.TYPE),c=NovaInventoryReflection.method(t,"clone");
        b.extension(t,"hasDamage",f->f.returns(Boolean.class).invoke(a->NovaInventoryReflection.invoke(h,a[0]))); b.extension(t,"damage",f->f.returns(Integer.class).invoke(a->NovaInventoryReflection.invoke(g,a[0]))); b.extension(t,"setDamage",f->f.param("damage",Integer.class).returns(Void.TYPE).invoke(a->NovaInventoryReflection.invoke(s,a[0],a[1])));
        b.extension(t,"hasMaxDamage",f->f.returns(Boolean.class).invoke(a->NovaInventoryReflection.invoke(hm,a[0]))); b.extension(t,"maxDamage",f->f.returns(Integer.class).invoke(a->NovaInventoryReflection.invoke(gm,a[0]))); b.extension(t,"setMaxDamage",f->f.param("maxDamage",Integer.class).returns(Void.TYPE).invoke(a->NovaInventoryReflection.invoke(sm,a[0],a[1]))); b.extension(t,"clone",f->f.returns(JavaTypeRef.javaType(t)).invoke(a->NovaInventoryReflection.invoke(c,a[0])));
    }
}
