package com.novalang.bukkit.types.value;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;

/** Spigot 1.12.2 FireworkEffect 及 Builder 的 Fluxon 值对象别名。 */
public final class NovaFireworkEffect {

    private NovaFireworkEffect() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(FireworkEffect.class, "hasFlicker", f -> f.returns(Boolean.class).invoke(a -> effect(a).hasFlicker()));
        builder.extension(FireworkEffect.class, "hasTrail", f -> f.returns(Boolean.class).invoke(a -> effect(a).hasTrail()));
        builder.extension(FireworkEffect.class, "colors", f -> f.returns(java.util.List.class).invoke(a -> effect(a).getColors()));
        builder.extension(FireworkEffect.class, "fadeColors", f -> f.returns(java.util.List.class).invoke(a -> effect(a).getFadeColors()));
        builder.extension(FireworkEffect.class, "type", f -> f.returns(FireworkEffect.Type.class).invoke(a -> effect(a).getType()));
        builder.extension(FireworkEffect.class, "serialize", f -> f.returns(java.util.Map.class).invoke(a -> effect(a).serialize()));
        builder.extension(FireworkEffect.class, "toString", f -> f.returns(String.class).invoke(a -> effect(a).toString()));
        builder.extension(FireworkEffect.class, "deserialize", f -> f.param("map", java.util.Map.class).returns(FireworkEffect.class).invoke(NovaFireworkEffect::deserialize));
        builder.extension(FireworkEffect.class, "builder", f -> f.returns(FireworkEffect.Builder.class).invoke(a -> FireworkEffect.builder()));
        builder.extension(FireworkEffect.Builder.class, "with", f -> f.param("type", FireworkEffect.Type.class).returns(FireworkEffect.Builder.class).invoke(a -> builder(a).with(NovaTypeSupport.argument(a, 1, FireworkEffect.Type.class))));
        builder.extension(FireworkEffect.Builder.class, "with", f -> f.param("type", String.class).returns(FireworkEffect.Builder.class).invoke(NovaFireworkEffect::withTypeName));
        builder.extension(FireworkEffect.Builder.class, "withFlicker", f -> f.returns(FireworkEffect.Builder.class).invoke(a -> builder(a).withFlicker()));
        builder.extension(FireworkEffect.Builder.class, "flicker", f -> f.param("flicker", Boolean.class).returns(FireworkEffect.Builder.class).invoke(a -> builder(a).flicker(NovaTypeSupport.argument(a, 1, Boolean.class))));
        builder.extension(FireworkEffect.Builder.class, "withTrail", f -> f.returns(FireworkEffect.Builder.class).invoke(a -> builder(a).withTrail()));
        builder.extension(FireworkEffect.Builder.class, "trail", f -> f.param("trail", Boolean.class).returns(FireworkEffect.Builder.class).invoke(a -> builder(a).trail(NovaTypeSupport.argument(a, 1, Boolean.class))));
        builder.extension(FireworkEffect.Builder.class, "withColor", f -> f.param("color", Color.class).returns(FireworkEffect.Builder.class).invoke(a -> builder(a).withColor(NovaTypeSupport.argument(a, 1, Color.class))));
        builder.extension(FireworkEffect.Builder.class, "withFade", f -> f.param("color", Color.class).returns(FireworkEffect.Builder.class).invoke(a -> builder(a).withFade(NovaTypeSupport.argument(a, 1, Color.class))));
        builder.extension(FireworkEffect.Builder.class, "build", f -> f.returns(FireworkEffect.class).invoke(a -> builder(a).build()));
    }

    private static FireworkEffect effect(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, FireworkEffect.class);
    }

    private static FireworkEffect.Builder builder(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, FireworkEffect.Builder.class);
    }

    private static FireworkEffect.Builder withTypeName(Object[] arguments) {
        FireworkEffect.Type type = NovaTypeSupport.findEnum(FireworkEffect.Type.class, NovaTypeSupport.argument(arguments, 1, String.class));
        if (type == null) {
            throw new IllegalArgumentException("烟花类型不存在");
        }
        return builder(arguments).with(type);
    }

    private static FireworkEffect deserialize(Object[] arguments) {
        java.util.Map<?, ?> source = NovaTypeSupport.argument(arguments, 1, java.util.Map.class);
        java.util.Map<String, Object> values = new java.util.HashMap<>();
        for (java.util.Map.Entry<?, ?> entry : source.entrySet()) {
            if (entry.getKey() instanceof String) {
                values.put((String) entry.getKey(), entry.getValue());
            }
        }
        return (FireworkEffect) FireworkEffect.deserialize(values);
    }
}
