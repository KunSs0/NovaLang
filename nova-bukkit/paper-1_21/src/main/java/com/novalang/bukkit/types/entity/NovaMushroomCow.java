package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import java.lang.reflect.Method;
import java.util.List;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/** 现代 MushroomCow 的 Fluxon 函数契约。 */
@Requires(classes = {"org.bukkit.entity.MushroomCow", "org.bukkit.entity.MushroomCow$Variant"}, methods = {"org.bukkit.entity.MushroomCow#hasEffectsForNextStew", "org.bukkit.entity.MushroomCow#getEffectsForNextStew", "org.bukkit.entity.MushroomCow#addEffectToNextStew", "org.bukkit.entity.MushroomCow#removeEffectFromNextStew", "org.bukkit.entity.MushroomCow#hasEffectForNextStew", "org.bukkit.entity.MushroomCow#clearEffectsForNextStew", "org.bukkit.entity.MushroomCow#getVariant", "org.bukkit.entity.MushroomCow#setVariant"})
public final class NovaMushroomCow {
    private static final String TYPE = "org.bukkit.entity.MushroomCow";
    private static final String VARIANT = "org.bukkit.entity.MushroomCow$Variant";
    private NovaMushroomCow() { }
    public static void register(JavaTypes.Builder builder) {
        Class<?> type = PaperEntityReflection.type(NovaMushroomCow.class, TYPE); Class<?> variant = PaperEntityReflection.type(NovaMushroomCow.class, VARIANT);
        Method has = PaperEntityReflection.method(type, "hasEffectsForNextStew"); Method effects = PaperEntityReflection.method(type, "getEffectsForNextStew"); Method add = PaperEntityReflection.method(type, "addEffectToNextStew", PotionEffect.class, Boolean.TYPE); Method remove = PaperEntityReflection.method(type, "removeEffectFromNextStew", PotionEffectType.class); Method hasEffect = PaperEntityReflection.method(type, "hasEffectForNextStew", PotionEffectType.class); Method clear = PaperEntityReflection.method(type, "clearEffectsForNextStew"); Method getVariant = PaperEntityReflection.method(type, "getVariant"); Method setVariant = PaperEntityReflection.method(type, "setVariant", variant); JavaTypeRef variantRef = JavaTypeRef.javaType(variant);
        builder.extension(type, "hasEffectsForNextStew", f -> f.returns(Boolean.class).invoke(a -> PaperEntityReflection.invoke(has, a[0]))); builder.extension(type, "effectsForNextStew", f -> f.returns(List.class).invoke(a -> PaperEntityReflection.invoke(effects, a[0]))); builder.extension(type, "addEffectToNextStew", f -> f.param("effect", PotionEffect.class).param("overwrite", Boolean.class).returns(Boolean.class).invoke(a -> PaperEntityReflection.invoke(add, a[0], a[1], a[2]))); builder.extension(type, "removeEffectFromNextStew", f -> f.param("type", PotionEffectType.class).returns(Boolean.class).invoke(a -> PaperEntityReflection.invoke(remove, a[0], a[1]))); builder.extension(type, "hasEffectForNextStew", f -> f.param("type", PotionEffectType.class).returns(Boolean.class).invoke(a -> PaperEntityReflection.invoke(hasEffect, a[0], a[1]))); builder.extension(type, "clearEffectsForNextStew", f -> f.returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(clear, a[0]))); builder.extension(type, "variant", f -> f.returns(variantRef).invoke(a -> PaperEntityReflection.invoke(getVariant, a[0]))); builder.extension(type, "setVariant", f -> f.param("variant", variantRef).returns(Void.TYPE).invoke(a -> PaperEntityReflection.invoke(setVariant, a[0], a[1]))); builder.extension(type, "setVariant", f -> f.param("variant", String.class).returns(Void.TYPE).invoke(a -> setVariant(setVariant, variant, a[0], (String) a[1])));
    }
    private static Object setVariant(Method setter, Class<?> variant, Object target, String name) { Object value = PaperEntityReflection.enumValue(variant, name); if (value == null) { return null; } return PaperEntityReflection.invoke(setter, target, value); }
}
