package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Beacon;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

/** Beacon 方块状态在 Spigot 1.12.2 可用的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.block.Beacon"})
final class NovaBeacon {

    private NovaBeacon() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableEffect = JavaTypeRef.javaType(PotionEffect.class).nullable();
        builder.extension(Beacon.class, "entitiesInRange", function -> function.returns(Collection.class)
                .invoke(arguments -> beacon(arguments).getEntitiesInRange()));
        builder.extension(Beacon.class, "tier", function -> function.returns(Integer.class)
                .invoke(arguments -> beacon(arguments).getTier()));
        builder.extension(Beacon.class, "primaryEffect", function -> function.returns(nullableEffect)
                .invoke(arguments -> beacon(arguments).getPrimaryEffect()));
        builder.extension(Beacon.class, "setPrimaryEffect", function -> function.param("effect", PotionEffectType.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    beacon(arguments).setPrimaryEffect(NovaTypeSupport.argument(arguments, 1, PotionEffectType.class));
                    return null;
                }));
        builder.extension(Beacon.class, "secondaryEffect", function -> function.returns(nullableEffect)
                .invoke(arguments -> beacon(arguments).getSecondaryEffect()));
        builder.extension(Beacon.class, "setSecondaryEffect", function -> function.param("effect", PotionEffectType.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    beacon(arguments).setSecondaryEffect(NovaTypeSupport.argument(arguments, 1, PotionEffectType.class));
                    return null;
                }));
    }

    private static Beacon beacon(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Beacon.class);
    }
}
