package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.GrassSpecies;

/** Spigot 1.12.2 GrassSpecies 的 Fluxon 函数别名。 */
@SuppressWarnings("deprecation")
public final class NovaGrassSpecies {

    private NovaGrassSpecies() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(GrassSpecies.class, "data", function -> function
                .returns(Integer.class)
                .invoke(arguments -> (int) grassSpecies(arguments).getData()));
        builder.extension(GrassSpecies.class, "getByData", function -> function
                .param("data", Integer.class)
                .returns(JavaTypeRef.javaType(GrassSpecies.class).nullable())
                .invoke(arguments -> GrassSpecies.getByData(
                        NovaTypeSupport.argument(arguments, 1, Integer.class).byteValue())));
    }

    private static GrassSpecies grassSpecies(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, GrassSpecies.class);
    }
}
