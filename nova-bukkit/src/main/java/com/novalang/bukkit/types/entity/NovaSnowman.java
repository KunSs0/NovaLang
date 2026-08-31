package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Snowman;

/** Spigot 1.12.2 雪傀儡的 Fluxon 函数别名。 */
@Requires(classes = {"org.bukkit.entity.Snowman"})
final class NovaSnowman {

    private NovaSnowman() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Snowman.class, "isDerp", function -> function.returns(Boolean.class)
                .invoke(arguments -> snowman(arguments).isDerp()));
        builder.extension(Snowman.class, "setDerp", function -> function.param("derp", Boolean.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    snowman(arguments).setDerp(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
    }

    private static Snowman snowman(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Snowman.class);
    }
}
