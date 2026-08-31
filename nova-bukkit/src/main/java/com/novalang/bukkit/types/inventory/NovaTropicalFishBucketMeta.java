package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.DyeColor;

import java.lang.reflect.Method;
import java.util.Locale;

/** 1.13+ TropicalFishBucketMeta 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.inventory.meta.TropicalFishBucketMeta", "org.bukkit.entity.TropicalFish$Pattern"}, methods = {
        "org.bukkit.inventory.meta.TropicalFishBucketMeta#getPatternColor", "org.bukkit.inventory.meta.TropicalFishBucketMeta#setPatternColor",
        "org.bukkit.inventory.meta.TropicalFishBucketMeta#getBodyColor", "org.bukkit.inventory.meta.TropicalFishBucketMeta#setBodyColor",
        "org.bukkit.inventory.meta.TropicalFishBucketMeta#setPattern", "org.bukkit.inventory.meta.TropicalFishBucketMeta#hasVariant",
        "org.bukkit.inventory.meta.TropicalFishBucketMeta#clone"})
public final class NovaTropicalFishBucketMeta {
    private static final String TYPE = "org.bukkit.inventory.meta.TropicalFishBucketMeta";
    private static final String PATTERN = "org.bukkit.entity.TropicalFish$Pattern";
    private NovaTropicalFishBucketMeta() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaInventoryReflection.type(NovaTropicalFishBucketMeta.class, TYPE);
        Class<?> pattern = NovaInventoryReflection.type(NovaTropicalFishBucketMeta.class, PATTERN);
        Method getPatternColor = NovaInventoryReflection.method(type, "getPatternColor"); Method setPatternColor = NovaInventoryReflection.method(type, "setPatternColor", DyeColor.class);
        Method getBodyColor = NovaInventoryReflection.method(type, "getBodyColor"); Method setBodyColor = NovaInventoryReflection.method(type, "setBodyColor", DyeColor.class);
        Method setPattern = NovaInventoryReflection.method(type, "setPattern", pattern); Method has = NovaInventoryReflection.method(type, "hasVariant"); Method clone = NovaInventoryReflection.method(type, "clone");
        builder.extension(type, "patternColor", f -> f.returns(JavaTypeRef.javaType(DyeColor.class).nullable()).invoke(a -> NovaInventoryReflection.invoke(getPatternColor, a[0])));
        builder.extension(type, "setPatternColor", f -> f.param("color", DyeColor.class).returns(Void.TYPE).invoke(a -> NovaInventoryReflection.invoke(setPatternColor, a[0], a[1])));
        builder.extension(type, "bodyColor", f -> f.returns(JavaTypeRef.javaType(DyeColor.class).nullable()).invoke(a -> NovaInventoryReflection.invoke(getBodyColor, a[0])));
        builder.extension(type, "setBodyColor", f -> f.param("color", DyeColor.class).returns(Void.TYPE).invoke(a -> NovaInventoryReflection.invoke(setBodyColor, a[0], a[1])));
        builder.extension(type, "setPattern", f -> f.param("pattern", pattern).returns(Void.TYPE).invoke(a -> NovaInventoryReflection.invoke(setPattern, a[0], a[1])));
        builder.extension(type, "setPattern", f -> f.param("pattern", String.class).returns(Void.TYPE).invoke(a -> setEnum(setPattern, pattern, a)));
        builder.extension(type, "hasVariant", f -> f.returns(Boolean.class).invoke(a -> NovaInventoryReflection.invoke(has, a[0])));
        builder.extension(type, "clone", f -> f.returns(JavaTypeRef.javaType(type)).invoke(a -> NovaInventoryReflection.invoke(clone, a[0])));
    }
    private static Object setEnum(Method method, Class<?> type, Object[] arguments) { Object value = enumValue(type, (String) arguments[1]); if (value != null) { NovaInventoryReflection.invoke(method, arguments[0], value); } return null; }
    private static Object enumValue(Class<?> type, String value) { if (!type.isEnum() || value == null) { return null; } String name = value.trim().replace(' ', '_').replace('.', '_').toUpperCase(Locale.ROOT); for (Object constant : type.getEnumConstants()) { if (((Enum<?>) constant).name().equals(name)) { return constant; } } return null; }
}
