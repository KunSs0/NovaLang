package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.Leaves;

/** 旧版 Leaves 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Leaves"})
final class NovaLegacyLeaves {

    private NovaLegacyLeaves() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Leaves.class, "isDecaying", function -> function.returns(Boolean.class).invoke(arguments -> leaves(arguments).isDecaying()));
        builder.extension(Leaves.class, "setDecaying", function -> function.param("decaying", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { leaves(arguments).setDecaying(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(Leaves.class, "isDecayable", function -> function.returns(Boolean.class).invoke(arguments -> leaves(arguments).isDecayable()));
        builder.extension(Leaves.class, "setDecayable", function -> function.param("decayable", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { leaves(arguments).setDecayable(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(Leaves.class, "toString", function -> function.returns(String.class).invoke(arguments -> leaves(arguments).toString()));
        builder.extension(Leaves.class, "clone", function -> function.returns(Leaves.class).invoke(arguments -> leaves(arguments).clone()));
    }

    private static Leaves leaves(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Leaves.class);
    }
}
