package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** 旧版方块 MaterialData 枚举补充聚合器。 */
public final class NovaLegacyEnumMoreTypes {
    private NovaLegacyEnumMoreTypes() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaLegacyMaterialEnum.class, NovaLegacyMaterialEnum::register);
    }
}
