package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.DyeColor;
import org.bukkit.entity.Wolf;

/** Spigot 1.12.2 狼的 Fluxon 函数别名。 */
public final class NovaWolf {

    private NovaWolf() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Wolf.class, "isAngry", function -> function.returns(Boolean.class).invoke(arguments -> wolf(arguments).isAngry()));
        builder.extension(Wolf.class, "setAngry", function -> function.param("angry", Boolean.class).returns(Void.TYPE).invoke(arguments -> {
            wolf(arguments).setAngry(argument(arguments, 1, Boolean.class));
            return null;
        }));
        builder.extension(Wolf.class, "collarColor", function -> function.returns(DyeColor.class).invoke(arguments -> wolf(arguments).getCollarColor()));
        builder.extension(Wolf.class, "setCollarColor", function -> function.param("color", DyeColor.class).returns(Void.TYPE).invoke(arguments -> {
            wolf(arguments).setCollarColor(argument(arguments, 1, DyeColor.class));
            return null;
        }));
    }

    private static Wolf wolf(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Wolf.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
