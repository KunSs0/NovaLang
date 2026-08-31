package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.Furnace;

/** 旧版 Furnace 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Furnace"})
final class NovaLegacyFurnace {

    private NovaLegacyFurnace() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Furnace.class, "clone", function -> function.returns(Furnace.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Furnace.class).clone()));
    }
}
