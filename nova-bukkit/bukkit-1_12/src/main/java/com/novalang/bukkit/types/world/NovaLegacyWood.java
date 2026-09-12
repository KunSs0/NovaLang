package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.TreeSpecies;
import org.bukkit.material.Wood;

/** 旧版 Wood 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Wood"})
final class NovaLegacyWood {

    private NovaLegacyWood() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Wood.class, "species", function -> function.returns(TreeSpecies.class).invoke(arguments -> wood(arguments).getSpecies()));
        builder.extension(Wood.class, "setSpecies", function -> function.param("species", TreeSpecies.class).returns(Void.TYPE)
                .invoke(arguments -> { wood(arguments).setSpecies(NovaTypeSupport.argument(arguments, 1, TreeSpecies.class)); return null; }));
        builder.extension(Wood.class, "toString", function -> function.returns(String.class).invoke(arguments -> wood(arguments).toString()));
        builder.extension(Wood.class, "clone", function -> function.returns(Wood.class).invoke(arguments -> wood(arguments).clone()));
    }

    private static Wood wood(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Wood.class);
    }
}
