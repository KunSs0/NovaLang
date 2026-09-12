package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.RedstoneTorch;

/** 旧版 RedstoneTorch 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.RedstoneTorch"})
final class NovaLegacyRedstoneTorch {

    private NovaLegacyRedstoneTorch() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(RedstoneTorch.class, "isPowered", function -> function.returns(Boolean.class).invoke(arguments -> torch(arguments).isPowered()));
        builder.extension(RedstoneTorch.class, "toString", function -> function.returns(String.class).invoke(arguments -> torch(arguments).toString()));
        builder.extension(RedstoneTorch.class, "clone", function -> function.returns(RedstoneTorch.class).invoke(arguments -> torch(arguments).clone()));
    }

    private static RedstoneTorch torch(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, RedstoneTorch.class);
    }
}
