package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.Cauldron;

/** Spigot 1.12.2 旧版炼药锅材料数据的 Fluxon 函数别名。 */
public final class NovaLegacyCauldron {

    private NovaLegacyCauldron() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Cauldron.class, "isFull", function -> function.returns(Boolean.class)
                .invoke(arguments -> cauldron(arguments).isFull()));
        builder.extension(Cauldron.class, "isEmpty", function -> function.returns(Boolean.class)
                .invoke(arguments -> cauldron(arguments).isEmpty()));
        builder.extension(Cauldron.class, "toString", function -> function.returns(String.class)
                .invoke(arguments -> cauldron(arguments).toString()));
        builder.extension(Cauldron.class, "clone", function -> function.returns(Cauldron.class)
                .invoke(arguments -> cauldron(arguments).clone()));
    }

    private static Cauldron cauldron(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Cauldron.class);
    }
}
