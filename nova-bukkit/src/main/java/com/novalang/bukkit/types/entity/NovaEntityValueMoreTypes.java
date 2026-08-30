package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 实体值对象补充聚合器。 */
public final class NovaEntityValueMoreTypes {
    private NovaEntityValueMoreTypes() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaFirework.class, NovaFirework::register);
    }
}
