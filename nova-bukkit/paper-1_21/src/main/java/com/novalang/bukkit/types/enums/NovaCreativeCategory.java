package com.novalang.bukkit.types.enums;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.inventory.CreativeCategory;

/** Paper 1.21.x CreativeCategory 枚举全局入口。 */
public final class NovaCreativeCategory {

    private NovaCreativeCategory() {
    }

    public static void register(JavaTypes.Builder builder) {
        Paper121EnumSupport.registerEnum(builder, "creativeCategory", CreativeCategory.class);
    }
}
