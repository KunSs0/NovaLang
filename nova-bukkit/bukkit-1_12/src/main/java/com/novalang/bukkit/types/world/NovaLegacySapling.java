package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.Sapling;

/** 旧版 Sapling 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Sapling"})
final class NovaLegacySapling {

    private NovaLegacySapling() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Sapling.class, "isInstantGrowable", function -> function.returns(Boolean.class).invoke(arguments -> sapling(arguments).isInstantGrowable()));
        builder.extension(Sapling.class, "setIsInstantGrowable", function -> function.param("instantGrowable", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { sapling(arguments).setIsInstantGrowable(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(Sapling.class, "toString", function -> function.returns(String.class).invoke(arguments -> sapling(arguments).toString()));
        builder.extension(Sapling.class, "clone", function -> function.returns(Sapling.class).invoke(arguments -> sapling(arguments).clone()));
    }

    private static Sapling sapling(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Sapling.class);
    }
}
