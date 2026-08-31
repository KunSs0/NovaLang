package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;

/** 1.20.5+ ColorableArmorMeta 的 Fluxon 克隆契约。 */
@Requires(classes={"org.bukkit.inventory.meta.ColorableArmorMeta"},methods={"org.bukkit.inventory.meta.ColorableArmorMeta#clone"})
public final class NovaColorableArmorMeta {
    private static final String TYPE="org.bukkit.inventory.meta.ColorableArmorMeta";
    private NovaColorableArmorMeta() { }
    public static void register(JavaTypes.Builder b) { Class<?> t=NovaInventoryReflection.type(NovaColorableArmorMeta.class,TYPE); Method c=NovaInventoryReflection.method(t,"clone"); b.extension(t,"clone",f->f.returns(JavaTypeRef.javaType(t)).invoke(a->NovaInventoryReflection.invoke(c,a[0]))); }
}
