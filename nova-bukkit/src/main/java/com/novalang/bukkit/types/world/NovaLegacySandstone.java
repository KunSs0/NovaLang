package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.SandstoneType;
import org.bukkit.material.Sandstone;

/** 旧版 Sandstone 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Sandstone"})
final class NovaLegacySandstone {

    private NovaLegacySandstone() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Sandstone.class, "type", function -> function.returns(SandstoneType.class).invoke(arguments -> sandstone(arguments).getType()));
        builder.extension(Sandstone.class, "setType", function -> function.param("type", SandstoneType.class).returns(Void.TYPE)
                .invoke(arguments -> { sandstone(arguments).setType(NovaTypeSupport.argument(arguments, 1, SandstoneType.class)); return null; }));
        builder.extension(Sandstone.class, "toString", function -> function.returns(String.class).invoke(arguments -> sandstone(arguments).toString()));
        builder.extension(Sandstone.class, "clone", function -> function.returns(Sandstone.class).invoke(arguments -> sandstone(arguments).clone()));
    }

    private static Sandstone sandstone(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Sandstone.class);
    }
}
