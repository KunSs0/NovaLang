package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.entity.EntityDamageEvent;

/** Spigot 1.12.2 EntityDamageEvent 伤害修正器别名。 */
@Requires(classes = {"org.bukkit.event.entity.EntityDamageEvent$DamageModifier"})
@SuppressWarnings("deprecation")
public final class NovaEntityDamageModifiers {

    private NovaEntityDamageModifiers() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityDamageEvent.class, "getOriginalDamage", function -> function.param("modifier", EntityDamageEvent.DamageModifier.class).returns(Double.class).invoke(arguments -> event(arguments).getOriginalDamage(NovaTypeSupport.argument(arguments, 1, EntityDamageEvent.DamageModifier.class))));
        builder.extension(EntityDamageEvent.class, "getOriginalDamage", function -> function.param("modifier", String.class).returns(Double.class).invoke(NovaEntityDamageModifiers::getOriginalDamage));
        builder.extension(EntityDamageEvent.class, "setDamage", function -> function.param("modifier", EntityDamageEvent.DamageModifier.class).param("damage", Double.class).returns(Void.TYPE).invoke(arguments -> {
            event(arguments).setDamage(NovaTypeSupport.argument(arguments, 1, EntityDamageEvent.DamageModifier.class), NovaTypeSupport.argument(arguments, 2, Double.class));
            return null;
        }));
        builder.extension(EntityDamageEvent.class, "setDamage", function -> function.param("modifier", String.class).param("damage", Double.class).returns(Void.TYPE).invoke(NovaEntityDamageModifiers::setDamage));
        builder.extension(EntityDamageEvent.class, "getDamage", function -> function.param("modifier", EntityDamageEvent.DamageModifier.class).returns(Double.class).invoke(arguments -> event(arguments).getDamage(NovaTypeSupport.argument(arguments, 1, EntityDamageEvent.DamageModifier.class))));
        builder.extension(EntityDamageEvent.class, "getDamage", function -> function.param("modifier", String.class).returns(Double.class).invoke(NovaEntityDamageModifiers::getDamage));
        builder.extension(EntityDamageEvent.class, "isApplicable", function -> function.param("modifier", EntityDamageEvent.DamageModifier.class).returns(Boolean.class).invoke(arguments -> event(arguments).isApplicable(NovaTypeSupport.argument(arguments, 1, EntityDamageEvent.DamageModifier.class))));
        builder.extension(EntityDamageEvent.class, "isApplicable", function -> function.param("modifier", String.class).returns(Boolean.class).invoke(NovaEntityDamageModifiers::isApplicable));
    }

    private static Object setDamage(Object[] arguments) {
        EntityDamageEvent.DamageModifier modifier = modifier(arguments);
        if (modifier != null) {
            event(arguments).setDamage(modifier, NovaTypeSupport.argument(arguments, 2, Double.class));
        }
        return null;
    }

    private static Double getOriginalDamage(Object[] arguments) {
        EntityDamageEvent.DamageModifier modifier = modifier(arguments);
        if (modifier == null) {
            return 0.0D;
        }
        return event(arguments).getOriginalDamage(modifier);
    }

    private static Double getDamage(Object[] arguments) {
        EntityDamageEvent.DamageModifier modifier = modifier(arguments);
        if (modifier == null) {
            return 0.0D;
        }
        return event(arguments).getDamage(modifier);
    }

    private static Boolean isApplicable(Object[] arguments) {
        EntityDamageEvent.DamageModifier modifier = modifier(arguments);
        if (modifier == null) {
            return false;
        }
        return event(arguments).isApplicable(modifier);
    }

    private static EntityDamageEvent.DamageModifier modifier(Object[] arguments) {
        return NovaTypeSupport.findEnum(EntityDamageEvent.DamageModifier.class, NovaTypeSupport.argument(arguments, 1, String.class));
    }

    private static EntityDamageEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityDamageEvent.class);
    }
}
