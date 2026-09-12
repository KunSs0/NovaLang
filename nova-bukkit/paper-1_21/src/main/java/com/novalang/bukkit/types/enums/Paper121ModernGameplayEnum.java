package com.novalang.bukkit.types.enums;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

/** Paper 1.21.x 属性类型的全局查询入口。 */
public final class Paper121ModernGameplayEnum {

    private Paper121ModernGameplayEnum() {
    }

    /** 注册属性及属性修改器操作查询函数。 */
    public static void register(JavaTypes.Builder builder) {
        Paper121EnumSupport.registerOldEnum(builder, "attribute", Attribute.class, Attribute::valueOf);
        Paper121EnumSupport.registerEnum(builder, "attributeModifierOperation", AttributeModifier.Operation.class);
    }
}
