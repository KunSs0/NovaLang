package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Guardian;

/** Spigot 1.12.2 中守卫者的 Fluxon 函数别名。 */
@Requires(classes = {"org.bukkit.entity.Guardian"})
public final class NovaGuardian {

    private NovaGuardian() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Guardian.class, "isElder", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> guardian(arguments).isElder()));
        builder.extension(Guardian.class, "setElder", function -> function
                .param("elder", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    guardian(arguments).setElder(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
    }

    private static Guardian guardian(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Guardian.class);
    }
}
