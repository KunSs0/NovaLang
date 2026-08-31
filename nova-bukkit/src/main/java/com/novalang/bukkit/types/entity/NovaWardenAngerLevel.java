package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;

/** 1.19+ Warden.AngerLevel 的 Fluxon 枚举查询入口。 */
@Requires(classes = {"org.bukkit.entity.Warden$AngerLevel"})
public final class NovaWardenAngerLevel {

    private static final String TYPE = "org.bukkit.entity.Warden$AngerLevel";

    private NovaWardenAngerLevel() {
    }

    public static void register(JavaTypes.Builder builder) {
        Class<?> type = NovaEntityReflection.type(NovaWardenAngerLevel.class, TYPE);
        NovaEntityReflection.registerEnum(builder, "wardenAngerLevel", type);
    }
}
