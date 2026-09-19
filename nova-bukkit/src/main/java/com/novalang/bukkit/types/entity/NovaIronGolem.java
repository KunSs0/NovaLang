package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.IronGolem;

/** Spigot 1.12.2 中铁傀儡的 Fluxon 函数别名。 */
@Requires(classes = {"org.bukkit.entity.IronGolem"})
public final class NovaIronGolem {

    private NovaIronGolem() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(IronGolem.class, "isPlayerCreated", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> ironGolem(arguments).isPlayerCreated()));
        builder.extension(IronGolem.class, "setPlayerCreated", function -> function
                .param("playerCreated", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    ironGolem(arguments).setPlayerCreated(
                            NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
    }

    private static IronGolem ironGolem(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, IronGolem.class);
    }
}
