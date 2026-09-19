package com.novalang.bukkit.types.enums;

import com.novalang.runtime.host.JavaTypes;

/** Bukkit 1.12 旧版实体枚举扩展的聚合注册器。 */
public final class Bukkit112LegacyEnumTypes {

    private Bukkit112LegacyEnumTypes() {
    }

    /** 注册 Bukkit 1.12 旧版实体枚举。 */
    public static void register(JavaTypes.Builder builder) {
        NovaGameplayEnum.register(builder);
        NovaEntityEnum.register(builder);
        NovaWorldInventoryEnum.register(builder);
    }
}
