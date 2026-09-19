package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.CoalType;
import org.bukkit.material.Coal;

/** 旧版 Coal 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Coal"})
final class NovaLegacyCoal {

    private NovaLegacyCoal() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Coal.class, "type", function -> function.returns(CoalType.class).invoke(arguments -> coal(arguments).getType()));
        builder.extension(Coal.class, "setType", function -> function.param("type", CoalType.class).returns(Void.TYPE)
                .invoke(arguments -> { coal(arguments).setType(NovaTypeSupport.argument(arguments, 1, CoalType.class)); return null; }));
        builder.extension(Coal.class, "toString", function -> function.returns(String.class).invoke(arguments -> coal(arguments).toString()));
        builder.extension(Coal.class, "clone", function -> function.returns(Coal.class).invoke(arguments -> coal(arguments).clone()));
    }

    private static Coal coal(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Coal.class);
    }
}
