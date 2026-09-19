package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.Redstone;

/** 旧版 Redstone 材料状态的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Redstone"})
final class NovaLegacyRedstone {

    private NovaLegacyRedstone() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Redstone.class, "isPowered", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Redstone.class).isPowered()));
    }
}
