package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Evoker;

/** Spigot 1.12.2 唤魔者的 Fluxon 函数别名。 */
public final class NovaEvoker {

    private NovaEvoker() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Evoker.class, "currentSpell", function -> function.returns(Evoker.Spell.class)
                .invoke(arguments -> evoker(arguments).getCurrentSpell()));
        builder.extension(Evoker.class, "setCurrentSpell", function -> function.param("spell", Evoker.Spell.class).returns(Void.TYPE)
                .invoke(arguments -> {
                    evoker(arguments).setCurrentSpell(argument(arguments, 1, Evoker.Spell.class));
                    return null;
                }));
    }

    private static Evoker evoker(Object[] arguments) {
        return argument(arguments, 0, Evoker.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
