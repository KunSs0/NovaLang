package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Bat;

/** Spigot 1.12.2 蝙蝠的 Fluxon 函数别名。 */
public final class NovaBat {

    private NovaBat() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Bat.class, "isAwake", function -> function.returns(Boolean.class).invoke(arguments -> bat(arguments).isAwake()));
        builder.extension(Bat.class, "setAwake", function -> function.param("awake", Boolean.class).returns(Void.TYPE).invoke(arguments -> {
            bat(arguments).setAwake(argument(arguments, 1, Boolean.class));
            return null;
        }));
    }

    private static Bat bat(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Bat.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
