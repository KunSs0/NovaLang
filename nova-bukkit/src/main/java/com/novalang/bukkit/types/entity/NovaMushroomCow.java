package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import java.util.List;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/** 现代 MushroomCow 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.MushroomCow", "org.bukkit.entity.MushroomCow$Variant"}, methods = {
        "org.bukkit.entity.MushroomCow#hasEffectsForNextStew", "org.bukkit.entity.MushroomCow#getEffectsForNextStew",
        "org.bukkit.entity.MushroomCow#addEffectToNextStew", "org.bukkit.entity.MushroomCow#removeEffectFromNextStew",
        "org.bukkit.entity.MushroomCow#hasEffectForNextStew", "org.bukkit.entity.MushroomCow#clearEffectsForNextStew",
        "org.bukkit.entity.MushroomCow#getVariant", "org.bukkit.entity.MushroomCow#setVariant"})
public final class NovaMushroomCow {
    private static final String TYPE = "org.bukkit.entity.MushroomCow";
    private static final String VARIANT = "org.bukkit.entity.MushroomCow$Variant";
    private NovaMushroomCow() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaMushroomCow.class, TYPE);
        Class<?> variant = NovaEntityReflection.type(NovaMushroomCow.class, VARIANT);
        Method has = NovaEntityReflection.method(type, "hasEffectsForNextStew");
        Method effects = NovaEntityReflection.method(type, "getEffectsForNextStew");
        Method add = NovaEntityReflection.method(type, "addEffectToNextStew", PotionEffect.class, Boolean.TYPE);
        Method remove = NovaEntityReflection.method(type, "removeEffectFromNextStew", PotionEffectType.class);
        Method hasEffect = NovaEntityReflection.method(type, "hasEffectForNextStew", PotionEffectType.class);
        Method clear = NovaEntityReflection.method(type, "clearEffectsForNextStew");
        Method getVariant = NovaEntityReflection.method(type, "getVariant");
        Method setVariant = NovaEntityReflection.method(type, "setVariant", variant);
        JavaTypeRef variantRef = JavaTypeRef.javaType(variant);
        builder.extension(type, "hasEffectsForNextStew", f -> f.returns(Boolean.class).invoke(a -> NovaEntityReflection.invoke(has, a[0])));
        builder.extension(type, "effectsForNextStew", f -> f.returns(List.class).invoke(a -> NovaEntityReflection.invoke(effects, a[0])));
        builder.extension(type, "addEffectToNextStew", f -> f.param("effect", PotionEffect.class).param("overwrite", Boolean.class).returns(Boolean.class).invoke(a -> NovaEntityReflection.invoke(add, a[0], a[1], a[2])));
        builder.extension(type, "removeEffectFromNextStew", f -> f.param("type", PotionEffectType.class).returns(Boolean.class).invoke(a -> NovaEntityReflection.invoke(remove, a[0], a[1])));
        builder.extension(type, "hasEffectForNextStew", f -> f.param("type", PotionEffectType.class).returns(Boolean.class).invoke(a -> NovaEntityReflection.invoke(hasEffect, a[0], a[1])));
        builder.extension(type, "clearEffectsForNextStew", f -> f.returns(Void.TYPE).invoke(a -> NovaEntityReflection.invoke(clear, a[0])));
        builder.extension(type, "variant", f -> f.returns(variantRef).invoke(a -> NovaEntityReflection.invoke(getVariant, a[0])));
        builder.extension(type, "setVariant", f -> f.param("variant", variantRef).returns(Void.TYPE).invoke(a -> NovaEntityReflection.invoke(setVariant, a[0], a[1])));
        builder.extension(type, "setVariant", f -> f.param("variant", String.class).returns(Void.TYPE).invoke(a -> setVariant(setVariant, variant, a[0], (String) a[1])));
    }
    private static Object setVariant(Method setter, Class<?> variant, Object target, String name) {
        Object value = NovaEntityReflection.enumValue(variant, name);
        if (value == null) { return null; }
        return NovaEntityReflection.invoke(setter, target, value);
    }
}
