package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Rabbit;

/** Spigot 1.12.2 兔子的 Fluxon 函数别名。 */
public final class NovaRabbit {

    private NovaRabbit() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Rabbit.class, "rabbitType", function -> function.returns(Rabbit.Type.class).invoke(arguments -> rabbit(arguments).getRabbitType()));
        builder.extension(Rabbit.class, "setRabbitType", function -> function.param("type", Rabbit.Type.class).returns(Void.TYPE).invoke(arguments -> {
            rabbit(arguments).setRabbitType(argument(arguments, 1, Rabbit.Type.class));
            return null;
        }));
    }

    private static Rabbit rabbit(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Rabbit.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
