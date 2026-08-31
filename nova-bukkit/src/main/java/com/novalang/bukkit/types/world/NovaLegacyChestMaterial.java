package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.Chest;

/** 旧版 Chest 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Chest"})
final class NovaLegacyChestMaterial {

    private NovaLegacyChestMaterial() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Chest.class, "clone", function -> function.returns(Chest.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Chest.class).clone()));
    }
}
