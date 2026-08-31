package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;

import java.lang.reflect.Method;
import java.util.Locale;

/** 1.17+ AxolotlBucketMeta 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.inventory.meta.AxolotlBucketMeta", "org.bukkit.entity.Axolotl$Variant"}, methods = {
        "org.bukkit.inventory.meta.AxolotlBucketMeta#setVariant", "org.bukkit.inventory.meta.AxolotlBucketMeta#hasVariant", "org.bukkit.inventory.meta.AxolotlBucketMeta#clone"})
public final class NovaAxolotlBucketMeta {
    private static final String TYPE = "org.bukkit.inventory.meta.AxolotlBucketMeta"; private static final String VARIANT = "org.bukkit.entity.Axolotl$Variant";
    private NovaAxolotlBucketMeta() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaInventoryReflection.type(NovaAxolotlBucketMeta.class, TYPE); Class<?> variant = NovaInventoryReflection.type(NovaAxolotlBucketMeta.class, VARIANT);
        Method set = NovaInventoryReflection.method(type, "setVariant", variant); Method has = NovaInventoryReflection.method(type, "hasVariant"); Method clone = NovaInventoryReflection.method(type, "clone");
        builder.extension(type, "setVariant", f -> f.param("variant", variant).returns(Void.TYPE).invoke(a -> NovaInventoryReflection.invoke(set, a[0], a[1])));
        builder.extension(type, "setVariant", f -> f.param("variant", String.class).returns(Void.TYPE).invoke(a -> { Object value = enumValue(variant, (String) a[1]); if (value != null) { NovaInventoryReflection.invoke(set, a[0], value); } return null; }));
        builder.extension(type, "hasVariant", f -> f.returns(Boolean.class).invoke(a -> NovaInventoryReflection.invoke(has, a[0])));
        builder.extension(type, "clone", f -> f.returns(JavaTypeRef.javaType(type)).invoke(a -> NovaInventoryReflection.invoke(clone, a[0])));
    }
    private static Object enumValue(Class<?> type, String value) { if (!type.isEnum() || value == null) { return null; } String name = value.trim().replace(' ', '_').replace('.', '_').toUpperCase(Locale.ROOT); for (Object constant : type.getEnumConstants()) { if (((Enum<?>) constant).name().equals(name)) { return constant; } } return null; }
}
