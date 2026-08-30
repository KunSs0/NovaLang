package com.novalang.bukkit.types.world;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** Spigot 1.12.2 独立方块状态类型补充聚合器。 */
public final class NovaBlockStateMoreTypes {
    private NovaBlockStateMoreTypes() { }
    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaBanner.class, NovaBanner::register);
        NovaBukkitRegistrar.register(builder, NovaFlowerPot.class, NovaFlowerPot::register);
        NovaBukkitRegistrar.register(builder, NovaEndGateway.class, NovaEndGateway::register);
    }
}
