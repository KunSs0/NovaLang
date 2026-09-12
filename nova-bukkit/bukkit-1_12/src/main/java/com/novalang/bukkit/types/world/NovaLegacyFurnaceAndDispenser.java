package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.FurnaceAndDispenser;

/** 旧版 FurnaceAndDispenser 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.FurnaceAndDispenser"})
final class NovaLegacyFurnaceAndDispenser {

    private NovaLegacyFurnaceAndDispenser() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(FurnaceAndDispenser.class, "clone", function -> function
                .returns(FurnaceAndDispenser.class)
                .invoke(arguments -> material(arguments).clone()));
    }

    private static FurnaceAndDispenser material(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, FurnaceAndDispenser.class);
    }
}
