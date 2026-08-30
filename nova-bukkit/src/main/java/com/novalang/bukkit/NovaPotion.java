package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionBrewer;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Collection;

/** Spigot 1.12.2 potion aliases. */
final class NovaPotion {

    private NovaPotion() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(PotionEffect.class, "apply", f -> f.param("entity", LivingEntity.class).returns(Boolean.class).invoke(a -> effect(a).apply(arg(a, 1, LivingEntity.class))));
        builder.extension(PotionEffect.class, "amplifier", f -> f.returns(Integer.class).invoke(a -> effect(a).getAmplifier()));
        builder.extension(PotionEffect.class, "duration", f -> f.returns(Integer.class).invoke(a -> effect(a).getDuration()));
        builder.extension(PotionEffect.class, "type", f -> f.returns(PotionEffectType.class).invoke(a -> effect(a).getType()));
        builder.extension(PotionEffect.class, "isAmbient", f -> f.returns(Boolean.class).invoke(a -> effect(a).isAmbient()));
        builder.extension(PotionEffect.class, "hasParticles", f -> f.returns(Boolean.class).invoke(a -> effect(a).hasParticles()));
        builder.extension(PotionEffect.class, "color", f -> f.returns(JavaTypeRef.javaType(Color.class).nullable()).invoke(a -> effect(a).getColor()));
        builder.extension(PotionEffect.class, "toString", f -> f.returns(String.class).invoke(a -> effect(a).toString()));
        builder.extension(PotionEffectType.class, "createEffect", f -> f.param("duration", Integer.class).param("amplifier", Integer.class).returns(PotionEffect.class).invoke(a -> effectType(a).createEffect(arg(a, 1, Integer.class), arg(a, 2, Integer.class))));
        builder.extension(PotionEffectType.class, "durationModifier", f -> f.returns(Double.class).invoke(a -> effectType(a).getDurationModifier()));
        builder.extension(PotionEffectType.class, "id", f -> f.returns(Integer.class).invoke(a -> effectType(a).getId()));
        builder.extension(PotionEffectType.class, "name", f -> f.returns(String.class).invoke(a -> effectType(a).getName()));
        builder.extension(PotionEffectType.class, "isInstant", f -> f.returns(Boolean.class).invoke(a -> effectType(a).isInstant()));
        builder.extension(PotionEffectType.class, "color", f -> f.returns(Color.class).invoke(a -> effectType(a).getColor()));
        builder.extension(PotionEffectType.class, "getById", f -> f.param("id", Integer.class).returns(JavaTypeRef.javaType(PotionEffectType.class).nullable()).invoke(a -> PotionEffectType.getById(arg(a, 1, Integer.class))));
        builder.extension(PotionEffectType.class, "getByName", f -> f.param("name", String.class).returns(JavaTypeRef.javaType(PotionEffectType.class).nullable()).invoke(a -> PotionEffectType.getByName(arg(a, 1, String.class))));
        builder.extension(PotionEffectType.class, "values", f -> f.returns(JavaTypeRef.javaType(PotionEffectType[].class)).invoke(a -> PotionEffectType.values()));
        builder.extension(PotionData.class, "type", f -> f.returns(PotionType.class).invoke(a -> data(a).getType()));
        builder.extension(PotionData.class, "isUpgraded", f -> f.returns(Boolean.class).invoke(a -> data(a).isUpgraded()));
        builder.extension(PotionData.class, "isExtended", f -> f.returns(Boolean.class).invoke(a -> data(a).isExtended()));
        builder.extension(PotionBrewer.class, "createEffect", f -> f.param("type", PotionEffectType.class).param("duration", Integer.class).param("amplifier", Integer.class).returns(PotionEffect.class).invoke(a -> brewer(a).createEffect(arg(a, 1, PotionEffectType.class), arg(a, 2, Integer.class), arg(a, 3, Integer.class))));
        builder.extension(PotionBrewer.class, "effectsFromDamage", f -> f.param("damage", Integer.class).returns(Collection.class).invoke(a -> brewer(a).getEffectsFromDamage(arg(a, 1, Integer.class))));
        builder.extension(PotionBrewer.class, "effects", f -> f.param("type", PotionType.class).param("upgraded", Boolean.class).param("extended", Boolean.class).returns(Collection.class).invoke(a -> brewer(a).getEffects(arg(a, 1, PotionType.class), arg(a, 2, Boolean.class), arg(a, 3, Boolean.class))));
    }

    private static PotionEffect effect(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, PotionEffect.class); }
    private static PotionEffectType effectType(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, PotionEffectType.class); }
    private static PotionData data(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, PotionData.class); }
    private static PotionBrewer brewer(Object[] arguments) { return NovaTypeSupport.argument(arguments, 0, PotionBrewer.class); }
    private static <T> T arg(Object[] arguments, int index, Class<T> type) { return NovaTypeSupport.argument(arguments, index, type); }
}
