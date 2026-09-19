package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.WitherSkull;

/** Spigot 1.12.2 凋灵头颅的 Fluxon 函数别名。 */
public final class NovaWitherSkull {

    private NovaWitherSkull() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(WitherSkull.class, "setCharged", function -> function.param("charged", Boolean.class).returns(Void.TYPE).invoke(arguments -> {
            skull(arguments).setCharged(argument(arguments, 1, Boolean.class));
            return null;
        }));
        builder.extension(WitherSkull.class, "isCharged", function -> function.returns(Boolean.class)
                .invoke(arguments -> skull(arguments).isCharged()));
    }

    private static WitherSkull skull(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, WitherSkull.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
