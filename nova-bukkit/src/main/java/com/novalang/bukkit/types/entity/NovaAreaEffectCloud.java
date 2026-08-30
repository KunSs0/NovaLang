package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

/** Spigot 1.12.2 区域效果云的 Fluxon 函数别名。 */
public final class NovaAreaEffectCloud {

    private NovaAreaEffectCloud() {
    }

    public static void register(JavaTypes.Builder builder) {
        intProperty(builder, "duration", AreaEffectCloud::getDuration, AreaEffectCloud::setDuration);
        intProperty(builder, "waitTime", AreaEffectCloud::getWaitTime, AreaEffectCloud::setWaitTime);
        intProperty(builder, "reapplicationDelay", AreaEffectCloud::getReapplicationDelay, AreaEffectCloud::setReapplicationDelay);
        intProperty(builder, "durationOnUse", AreaEffectCloud::getDurationOnUse, AreaEffectCloud::setDurationOnUse);
        floatProperty(builder, "radius", AreaEffectCloud::getRadius, AreaEffectCloud::setRadius);
        floatProperty(builder, "radiusOnUse", AreaEffectCloud::getRadiusOnUse, AreaEffectCloud::setRadiusOnUse);
        floatProperty(builder, "radiusPerTick", AreaEffectCloud::getRadiusPerTick, AreaEffectCloud::setRadiusPerTick);
        builder.extension(AreaEffectCloud.class, "particle", function -> function.returns(Particle.class)
                .invoke(arguments -> cloud(arguments).getParticle()));
        builder.extension(AreaEffectCloud.class, "setParticle", function -> function.param("particle", Particle.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    cloud(arguments).setParticle(argument(arguments, 1, Particle.class));
                    return null;
                }));
        builder.extension(AreaEffectCloud.class, "basePotionData", function -> function.returns(PotionData.class)
                .invoke(arguments -> cloud(arguments).getBasePotionData()));
        builder.extension(AreaEffectCloud.class, "setBasePotionData", function -> function.param("data", PotionData.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    cloud(arguments).setBasePotionData(argument(arguments, 1, PotionData.class));
                    return null;
                }));
        builder.extension(AreaEffectCloud.class, "hasCustomEffects", function -> function.returns(Boolean.class)
                .invoke(arguments -> cloud(arguments).hasCustomEffects()));
        builder.extension(AreaEffectCloud.class, "customEffects", function -> function.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(PotionEffect.class)))
                .invoke(arguments -> cloud(arguments).getCustomEffects()));
        builder.extension(AreaEffectCloud.class, "addCustomEffect", function -> function.param("effect", PotionEffect.class).param("overwrite", Boolean.class).returns(Boolean.class)
                .invoke(arguments -> cloud(arguments).addCustomEffect(argument(arguments, 1, PotionEffect.class), argument(arguments, 2, Boolean.class))));
        builder.extension(AreaEffectCloud.class, "removeCustomEffect", function -> function.param("type", PotionEffectType.class).returns(Boolean.class)
                .invoke(arguments -> cloud(arguments).removeCustomEffect(argument(arguments, 1, PotionEffectType.class))));
        builder.extension(AreaEffectCloud.class, "hasCustomEffect", function -> function.param("type", PotionEffectType.class).returns(Boolean.class)
                .invoke(arguments -> cloud(arguments).hasCustomEffect(argument(arguments, 1, PotionEffectType.class))));
        builder.extension(AreaEffectCloud.class, "clearCustomEffects", function -> function.returns(Void.TYPE).invoke(arguments -> {
            cloud(arguments).clearCustomEffects();
            return null;
        }));
        builder.extension(AreaEffectCloud.class, "color", function -> function.returns(Color.class)
                .invoke(arguments -> cloud(arguments).getColor()));
        builder.extension(AreaEffectCloud.class, "setColor", function -> function.param("color", Color.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    cloud(arguments).setColor(argument(arguments, 1, Color.class));
                    return null;
                }));
        builder.extension(AreaEffectCloud.class, "source", function -> function.returns(JavaTypeRef.javaType(ProjectileSource.class).nullable())
                .invoke(arguments -> cloud(arguments).getSource()));
        builder.extension(AreaEffectCloud.class, "setSource", function -> function.param("source", JavaTypeRef.javaType(ProjectileSource.class).nullable()).returns(Void.TYPE)
                .invoke(arguments -> {
                    cloud(arguments).setSource(argument(arguments, 1, ProjectileSource.class));
                    return null;
                }));
    }

    private static void intProperty(JavaTypes.Builder builder, String name, IntGetter getter, IntSetter setter) {
        builder.extension(AreaEffectCloud.class, name, function -> function.returns(Integer.class).invoke(arguments -> getter.get(cloud(arguments))));
        builder.extension(AreaEffectCloud.class, "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1), function -> function.param("value", Integer.class).returns(Void.TYPE).invoke(arguments -> {
            setter.set(cloud(arguments), argument(arguments, 1, Integer.class));
            return null;
        }));
    }

    private static void floatProperty(JavaTypes.Builder builder, String name, FloatGetter getter, FloatSetter setter) {
        builder.extension(AreaEffectCloud.class, name, function -> function.returns(Float.class).invoke(arguments -> getter.get(cloud(arguments))));
        builder.extension(AreaEffectCloud.class, "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1), function -> function.param("value", Float.class).returns(Void.TYPE).invoke(arguments -> {
            setter.set(cloud(arguments), argument(arguments, 1, Float.class));
            return null;
        }));
    }

    private static AreaEffectCloud cloud(Object[] arguments) {
        return argument(arguments, 0, AreaEffectCloud.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }

    private interface IntGetter {
        int get(AreaEffectCloud cloud);
    }

    private interface IntSetter {
        void set(AreaEffectCloud cloud, int value);
    }

    private interface FloatGetter {
        float get(AreaEffectCloud cloud);
    }

    private interface FloatSetter {
        void set(AreaEffectCloud cloud, float value);
    }
}
