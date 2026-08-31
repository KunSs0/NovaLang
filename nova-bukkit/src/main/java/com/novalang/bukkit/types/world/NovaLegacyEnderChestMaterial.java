package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.EnderChest;

/** 旧版 EnderChest 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.EnderChest"})
final class NovaLegacyEnderChestMaterial {

    private NovaLegacyEnderChestMaterial() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(EnderChest.class, "clone", function -> function.returns(EnderChest.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, EnderChest.class).clone()));
    }
}
