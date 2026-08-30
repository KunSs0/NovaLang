package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypes;

/** World、区块、方块及相关对象扩展的领域聚合器。 */
final class NovaWorldBlocks {

    private NovaWorldBlocks() {
    }

    static void register(JavaTypes.Builder builder) {
        NovaWorldExtra.register(builder);
        NovaChunk.register(builder);
        NovaBlock.register(builder);
        NovaBlockState.register(builder);
        NovaMaterialData.register(builder);
        NovaMap.register(builder);
        NovaGenerator.register(builder);
    }
}
