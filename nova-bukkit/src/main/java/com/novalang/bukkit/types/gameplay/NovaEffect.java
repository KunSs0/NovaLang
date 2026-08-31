package com.novalang.bukkit.types.gameplay;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Effect;

/** Spigot 1.12.2 Effect 的 Fluxon 函数别名。 */
public final class NovaEffect {

    private NovaEffect() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Effect.class, "id", function -> function
                .returns(Integer.class)
                .invoke(arguments -> effect(arguments).getId()));
        builder.extension(Effect.class, "type", function -> function
                .returns(Effect.Type.class)
                .invoke(arguments -> effect(arguments).getType()));
        builder.extension(Effect.class, "data", function -> function
                .returns(JavaTypeRef.javaType(Class.class).nullable())
                .invoke(arguments -> effect(arguments).getData()));
    }

    private static Effect effect(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Effect.class);
    }
}
