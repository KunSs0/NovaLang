package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.GameMode;

/** Spigot 1.12.2 GameMode 的 Fluxon 函数别名。 */
@SuppressWarnings("deprecation")
public final class NovaGameMode {

    private NovaGameMode() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(GameMode.class, "value", function -> function
                .returns(Integer.class)
                .invoke(arguments -> gameMode(arguments).getValue()));
        builder.extension(GameMode.class, "getByValue", function -> function
                .param("value", Integer.class)
                .returns(JavaTypeRef.javaType(GameMode.class).nullable())
                .invoke(arguments -> GameMode.getByValue(
                        NovaTypeSupport.argument(arguments, 1, Integer.class))));
    }

    private static GameMode gameMode(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, GameMode.class);
    }
}
