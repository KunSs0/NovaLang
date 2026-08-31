package com.novalang.bukkit.types.gameplay;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.EntityEffect;

/** Spigot 1.12.2 EntityEffect 的 Fluxon 函数别名。 */
public final class NovaEntityEffect {

    private NovaEntityEffect() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityEffect.class, "data", function -> function
                .returns(Integer.class)
                .invoke(arguments -> (int) entityEffect(arguments).getData()));
    }

    private static EntityEffect entityEffect(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityEffect.class);
    }
}
