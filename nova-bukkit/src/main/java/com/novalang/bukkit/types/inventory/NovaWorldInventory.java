package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.types.value.NovaVector;
import com.novalang.bukkit.types.world.NovaChunkSnapshot;
import com.novalang.bukkit.types.world.NovaWorldObject;
import com.novalang.runtime.host.JavaTypes;

/** Bukkit 对象扩展聚合器；各领域实现保持独立注册器。 */
public final class NovaWorldInventory {

    private NovaWorldInventory() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaWorldObject.register(builder);
        NovaInventory.register(builder);
        NovaPlayerInventory.register(builder);
        NovaItemStack.register(builder);
        NovaItemMeta.register(builder);
        NovaMaterial.register(builder);
        NovaVector.register(builder);
        NovaChunkSnapshot.register(builder);
    }
}
