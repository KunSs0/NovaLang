package com.novalang.bukkit.types.world;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 世界生成工具补充聚合器。 */
public final class NovaWorldToolMoreTypes {
    private NovaWorldToolMoreTypes() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaBlockPopulator.class, NovaBlockPopulator::register);
    }
}
