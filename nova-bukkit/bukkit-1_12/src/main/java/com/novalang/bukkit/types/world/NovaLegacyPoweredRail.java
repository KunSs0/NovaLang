package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.PoweredRail;

/** 旧版 PoweredRail 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.PoweredRail"})
final class NovaLegacyPoweredRail {

    private NovaLegacyPoweredRail() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(PoweredRail.class, "isPowered", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> rail(arguments).isPowered()));
        builder.extension(PoweredRail.class, "setPowered", function -> function
                .param("powered", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    rail(arguments).setPowered(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(PoweredRail.class, "clone", function -> function
                .returns(PoweredRail.class)
                .invoke(arguments -> rail(arguments).clone()));
    }

    private static PoweredRail rail(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PoweredRail.class);
    }
}
