package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import java.lang.reflect.Method;

/** 1.19+ Frog 及 Variant 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.Frog", "org.bukkit.entity.Frog$Variant"}, methods = {"org.bukkit.entity.Frog#getTongueTarget", "org.bukkit.entity.Frog#setTongueTarget", "org.bukkit.entity.Frog#getVariant", "org.bukkit.entity.Frog#setVariant", "org.bukkit.entity.Frog$Variant#getKey"})
public final class NovaFrog {
    private static final String TYPE = "org.bukkit.entity.Frog"; private static final String VARIANT = "org.bukkit.entity.Frog$Variant";
    private NovaFrog() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaFrog.class, TYPE); Class<?> variant = NovaEntityReflection.type(NovaFrog.class, VARIANT);
        Method target = NovaEntityReflection.method(type, "getTongueTarget"); Method setTarget = NovaEntityReflection.method(type, "setTongueTarget", Entity.class); Method getVariant = NovaEntityReflection.method(type, "getVariant"); Method setVariant = NovaEntityReflection.method(type, "setVariant", variant); Method key = NovaEntityReflection.method(variant, "getKey");
        builder.extension(type, "tongueTarget", f -> f.returns(JavaTypeRef.javaType(Entity.class).nullable()).invoke(a -> NovaEntityReflection.invoke(target, a[0])));
        builder.extension(type, "setTongueTarget", f -> f.param("target", JavaTypeRef.javaType(Entity.class).nullable()).returns(Void.TYPE).invoke(a -> NovaEntityReflection.invoke(setTarget, a[0], a[1])));
        builder.extension(type, "variant", f -> f.returns(JavaTypeRef.javaType(variant)).invoke(a -> NovaEntityReflection.invoke(getVariant, a[0])));
        builder.extension(type, "setVariant", f -> f.param("variant", JavaTypeRef.javaType(variant)).returns(Void.TYPE).invoke(a -> NovaEntityReflection.invoke(setVariant, a[0], a[1])));
        builder.extension(variant, "key", f -> f.returns(NamespacedKey.class).invoke(a -> NovaEntityReflection.invoke(key, a[0])));
    }
}
