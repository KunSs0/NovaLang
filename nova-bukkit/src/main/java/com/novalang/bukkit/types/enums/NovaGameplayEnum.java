package com.novalang.bukkit.types.enums;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

/** 属性体系的 Spigot 1.12.2 Fluxon 枚举入口。 */
final class NovaGameplayEnum {

    private NovaGameplayEnum() {
    }

    static void register(JavaTypes.Builder builder) {
        NovaEnum.registerEnum(builder, "attribute", Attribute.class);
        NovaEnum.registerEnum(builder, "attributeModifierOperation", AttributeModifier.Operation.class);
    }
}
