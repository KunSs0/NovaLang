package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;

import java.util.Collection;
import java.util.Map;

/** Spigot 1.12.2 attribute aliases. */
final class NovaAttribute {

    private NovaAttribute() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableInstance = JavaTypeRef.javaType(AttributeInstance.class).nullable();
        JavaTypeRef nullableDouble = JavaTypeRef.javaType(Double.class).nullable();
        builder.extension(Attributable.class, "attribute", f -> f.param("attribute", Attribute.class).returns(nullableInstance).invoke(a -> attributable(a).getAttribute(arg(a, 1, Attribute.class))));
        registerConvenience(builder, nullableInstance, nullableDouble);
        builder.extension(AttributeInstance.class, "attribute", f -> f.returns(Attribute.class).invoke(a -> instance(a).getAttribute()));
        builder.extension(AttributeInstance.class, "baseValue", f -> f.returns(Double.class).invoke(a -> instance(a).getBaseValue()));
        builder.extension(AttributeInstance.class, "setBaseValue", f -> f.param("value", Double.class).returns(Void.TYPE).invoke(a -> { instance(a).setBaseValue(arg(a, 1, Double.class)); return null; }));
        builder.extension(AttributeInstance.class, "modifiers", f -> f.returns(Collection.class).invoke(a -> instance(a).getModifiers()));
        builder.extension(AttributeInstance.class, "addModifier", f -> f.param("modifier", AttributeModifier.class).returns(Void.TYPE).invoke(a -> { instance(a).addModifier(arg(a, 1, AttributeModifier.class)); return null; }));
        builder.extension(AttributeInstance.class, "removeModifier", f -> f.param("modifier", AttributeModifier.class).returns(Void.TYPE).invoke(a -> { instance(a).removeModifier(arg(a, 1, AttributeModifier.class)); return null; }));
        builder.extension(AttributeInstance.class, "value", f -> f.returns(Double.class).invoke(a -> instance(a).getValue()));
        builder.extension(AttributeInstance.class, "defaultValue", f -> f.returns(Double.class).invoke(a -> instance(a).getDefaultValue()));
        builder.extension(AttributeModifier.class, "uniqueId", f -> f.returns(java.util.UUID.class).invoke(a -> modifier(a).getUniqueId()));
        builder.extension(AttributeModifier.class, "name", f -> f.returns(String.class).invoke(a -> modifier(a).getName()));
        builder.extension(AttributeModifier.class, "amount", f -> f.returns(Double.class).invoke(a -> modifier(a).getAmount()));
        builder.extension(AttributeModifier.class, "operation", f -> f.returns(AttributeModifier.Operation.class).invoke(a -> modifier(a).getOperation()));
        builder.extension(AttributeModifier.class, "deserialize", f -> f.param("data", Map.class).returns(AttributeModifier.class).invoke(a -> AttributeModifier.deserialize(arg(a, 1, Map.class))));
    }

    private static void registerConvenience(JavaTypes.Builder b, JavaTypeRef nullableInstance, JavaTypeRef nullableDouble) {
        registerAttribute(b, "armor", "baseArmor", "setBaseArmor", Attribute.GENERIC_ARMOR, nullableInstance, nullableDouble);
        registerAttribute(b, "armorToughness", "baseArmorToughness", "setBaseArmorToughness", Attribute.GENERIC_ARMOR_TOUGHNESS, nullableInstance, nullableDouble);
        registerAttribute(b, "attackDamage", "baseAttackDamage", "setBaseAttackDamage", Attribute.GENERIC_ATTACK_DAMAGE, nullableInstance, nullableDouble);
        registerAttribute(b, "flyingSpeed", "baseFlyingSpeed", "setBaseFlyingSpeed", Attribute.GENERIC_FLYING_SPEED, nullableInstance, nullableDouble);
        registerAttribute(b, "followRange", "baseFollowRange", "setBaseFollowRange", Attribute.GENERIC_FOLLOW_RANGE, nullableInstance, nullableDouble);
        registerAttribute(b, "luck", "baseLuck", "setBaseLuck", Attribute.GENERIC_LUCK, nullableInstance, nullableDouble);
        registerAttribute(b, "maxHealth", "baseMaxHealth", "setBaseMaxHealth", Attribute.GENERIC_MAX_HEALTH, nullableInstance, nullableDouble);
        registerAttribute(b, "movementSpeed", "baseMovementSpeed", "setBaseMovementSpeed", Attribute.GENERIC_MOVEMENT_SPEED, nullableInstance, nullableDouble);
    }

    private static void registerAttribute(JavaTypes.Builder b, String valueName, String baseName, String setterName, Attribute attribute, JavaTypeRef nullableInstance, JavaTypeRef nullableDouble) {
        b.extension(Attributable.class, valueName, f -> f.returns(nullableDouble).invoke(a -> value(attributable(a), attribute, false)));
        b.extension(Attributable.class, baseName, f -> f.returns(nullableDouble).invoke(a -> value(attributable(a), attribute, true)));
        b.extension(Attributable.class, setterName, f -> f.param("value", Double.class).returns(nullableInstance).invoke(a -> setBase(attributable(a), attribute, arg(a, 1, Double.class))));
    }

    private static Double value(Attributable attributable, Attribute attribute, boolean base) {
        AttributeInstance instance = attributable.getAttribute(attribute);
        if (instance == null) {
            return null;
        }
        if (base) {
            return instance.getBaseValue();
        }
        return instance.getValue();
    }

    private static AttributeInstance setBase(Attributable attributable, Attribute attribute, Double value) {
        AttributeInstance instance = attributable.getAttribute(attribute);
        if (instance == null) {
            return null;
        }
        instance.setBaseValue(value);
        return instance;
    }

    private static Attributable attributable(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Attributable.class);
    }

    private static AttributeInstance instance(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, AttributeInstance.class);
    }

    private static AttributeModifier modifier(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, AttributeModifier.class);
    }

    private static <T> T arg(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
