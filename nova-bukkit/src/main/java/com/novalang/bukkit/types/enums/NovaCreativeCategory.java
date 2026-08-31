package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

/** 1.20.5+ CreativeCategory 枚举全局入口。 */
@Requires(classes = {"org.bukkit.inventory.CreativeCategory"})
@SuppressWarnings({"rawtypes", "unchecked"})
public final class NovaCreativeCategory {

    private static final String CREATIVE_CATEGORY = "org.bukkit.inventory.CreativeCategory";

    private NovaCreativeCategory() {
    }

    public static void register(JavaTypes.Builder builder) {
        try {
            Class<?> categoryType = Class.forName(CREATIVE_CATEGORY, false, NovaCreativeCategory.class.getClassLoader());
            NovaEnum.registerEnum(builder, "creativeCategory", (Class) categoryType);
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("已通过 Requires 校验的 Bukkit 类不存在: " + CREATIVE_CATEGORY, exception);
        }
    }
}
