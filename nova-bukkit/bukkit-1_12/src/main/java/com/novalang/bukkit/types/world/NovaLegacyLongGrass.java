package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.GrassSpecies;
import org.bukkit.material.LongGrass;

/** 旧版 LongGrass 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.LongGrass"})
final class NovaLegacyLongGrass {

    private NovaLegacyLongGrass() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(LongGrass.class, "species", function -> function.returns(GrassSpecies.class).invoke(arguments -> grass(arguments).getSpecies()));
        builder.extension(LongGrass.class, "setSpecies", function -> function.param("species", GrassSpecies.class).returns(Void.TYPE)
                .invoke(arguments -> { grass(arguments).setSpecies(NovaTypeSupport.argument(arguments, 1, GrassSpecies.class)); return null; }));
        builder.extension(LongGrass.class, "toString", function -> function.returns(String.class).invoke(arguments -> grass(arguments).toString()));
        builder.extension(LongGrass.class, "clone", function -> function.returns(LongGrass.class).invoke(arguments -> grass(arguments).clone()));
    }

    private static LongGrass grass(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, LongGrass.class);
    }
}
