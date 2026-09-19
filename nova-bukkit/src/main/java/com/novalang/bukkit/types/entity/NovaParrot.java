package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Parrot;

/** Spigot 1.12.2 鹦鹉的 Fluxon 函数别名。 */
public final class NovaParrot {

    private NovaParrot() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Parrot.class, "variant", function -> function.returns(Parrot.Variant.class)
                .invoke(arguments -> parrot(arguments).getVariant()));
        builder.extension(Parrot.class, "setVariant", function -> function.param("variant", Parrot.Variant.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    parrot(arguments).setVariant(argument(arguments, 1, Parrot.Variant.class));
                    return null;
                }));
    }

    private static Parrot parrot(Object[] arguments) {
        return argument(arguments, 0, Parrot.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
