package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Difficulty;

/** Spigot 1.12.2 Difficulty 的 Fluxon 函数别名。 */
@SuppressWarnings("deprecation")
public final class NovaDifficulty {

    private NovaDifficulty() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Difficulty.class, "value", function -> function
                .returns(Integer.class)
                .invoke(arguments -> difficulty(arguments).getValue()));
        builder.extension(Difficulty.class, "getByValue", function -> function
                .param("value", Integer.class)
                .returns(JavaTypeRef.javaType(Difficulty.class).nullable())
                .invoke(arguments -> Difficulty.getByValue(
                        NovaTypeSupport.argument(arguments, 1, Integer.class))));
    }

    private static Difficulty difficulty(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Difficulty.class);
    }
}
