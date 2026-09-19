package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.EnderDragon;

/** Spigot 1.12.2 末影龙的 Fluxon 函数别名。 */
public final class NovaEnderDragon {

    private NovaEnderDragon() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EnderDragon.class, "phase", function -> function.returns(EnderDragon.Phase.class)
                .invoke(arguments -> dragon(arguments).getPhase()));
        builder.extension(EnderDragon.class, "setPhase", function -> function.param("phase", EnderDragon.Phase.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    dragon(arguments).setPhase(argument(arguments, 1, EnderDragon.Phase.class));
                    return null;
                }));
    }

    private static EnderDragon dragon(Object[] arguments) {
        return argument(arguments, 0, EnderDragon.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
