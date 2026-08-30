package com.novalang.bukkit.types.world;

import com.novalang.bukkit.NovaBukkitRegistrar;
import com.novalang.runtime.host.JavaTypes;

/** 1.12.2 方块状态与物品元数据补充扩展聚合器。 */
public final class NovaBlockInventoryMoreTypes {

    private NovaBlockInventoryMoreTypes() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaBukkitRegistrar.register(builder, NovaDispenser.class, NovaDispenser::register);
        NovaBukkitRegistrar.register(builder, NovaDropper.class, NovaDropper::register);
        NovaBukkitRegistrar.register(builder, NovaChest.class, NovaChest::register);
        NovaBukkitRegistrar.register(builder, NovaDoubleChest.class, NovaDoubleChest::register);
        NovaBukkitRegistrar.register(builder, NovaSkull.class, NovaSkull::register);
    }
}
