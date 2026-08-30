package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Ocelot;

/** Spigot 1.12.2 豹猫的 Fluxon 函数别名。 */
public final class NovaOcelot {

    private NovaOcelot() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Ocelot.class, "catType", function -> function.returns(Ocelot.Type.class).invoke(arguments -> ocelot(arguments).getCatType()));
        builder.extension(Ocelot.class, "setCatType", function -> function.param("type", Ocelot.Type.class).returns(Void.TYPE).invoke(arguments -> {
            ocelot(arguments).setCatType(argument(arguments, 1, Ocelot.Type.class));
            return null;
        }));
    }

    private static Ocelot ocelot(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Ocelot.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
