package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkMeta;

@Requires(classes = {"org.bukkit.inventory.meta.FireworkMeta"})
public final class NovaFireworkMetaMoreTypes {
    private NovaFireworkMetaMoreTypes() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef effects = JavaTypeRef.parameterized("Iterable<FireworkEffect>", Iterable.class,
                JavaTypeRef.javaType(FireworkEffect.class));
        builder.extension(FireworkMeta.class, "addEffects", function -> function.param("effects", effects)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).addEffects(effects(arguments));
                    return null;
                }));
        builder.extension(FireworkMeta.class, "addEffects", function -> function
                .param("effects", FireworkEffect[].class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).addEffects(NovaTypeSupport.argument(arguments, 1, FireworkEffect[].class));
                    return null;
                }));
    }

    private static FireworkMeta meta(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, FireworkMeta.class);
    }

    @SuppressWarnings("unchecked")
    private static Iterable<FireworkEffect> effects(Object[] arguments) {
        return (Iterable<FireworkEffect>) NovaTypeSupport.argument(arguments, 1, Iterable.class);
    }
}
