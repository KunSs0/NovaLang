package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;

/** Spigot 1.12.2 Damageable 的 Fluxon 别名。 */
@SuppressWarnings("deprecation")
final class NovaDamageable {

    private NovaDamageable() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Damageable.class, "damage", f -> f.param("amount", Double.class).returns(Void.TYPE).invoke(a -> {
            damageable(a).damage(argument(a, 1, Double.class));
            return null;
        }));
        builder.extension(Damageable.class, "damage", f -> f.param("amount", Double.class).param("source", Entity.class).returns(Void.TYPE).invoke(a -> {
            damageable(a).damage(argument(a, 1, Double.class), argument(a, 2, Entity.class));
            return null;
        }));
        builder.extension(Damageable.class, "health", f -> f.returns(Double.class).invoke(a -> damageable(a).getHealth()));
        builder.extension(Damageable.class, "setHealth", f -> f.param("health", Double.class).returns(Void.TYPE).invoke(a -> {
            damageable(a).setHealth(argument(a, 1, Double.class));
            return null;
        }));
        builder.extension(Damageable.class, "setMaxHealth", f -> f.param("health", Double.class).returns(Void.TYPE).invoke(a -> {
            damageable(a).setMaxHealth(argument(a, 1, Double.class));
            return null;
        }));
        builder.extension(Damageable.class, "resetMaxHealth", f -> f.returns(Void.TYPE).invoke(a -> {
            damageable(a).resetMaxHealth();
            return null;
        }));
    }

    private static Damageable damageable(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Damageable.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
