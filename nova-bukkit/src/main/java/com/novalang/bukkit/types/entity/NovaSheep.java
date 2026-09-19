package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Sheep;

/** Spigot 1.12.2 绵羊的 Fluxon 函数别名。 */
public final class NovaSheep {

    private NovaSheep() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Sheep.class, "isSheared", function -> function.returns(Boolean.class).invoke(arguments -> sheep(arguments).isSheared()));
        builder.extension(Sheep.class, "setSheared", function -> function.param("sheared", Boolean.class).returns(Void.TYPE).invoke(arguments -> {
            sheep(arguments).setSheared(argument(arguments, 1, Boolean.class));
            return null;
        }));
    }

    private static Sheep sheep(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Sheep.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
