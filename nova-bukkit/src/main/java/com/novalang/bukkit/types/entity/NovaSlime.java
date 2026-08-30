package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Slime;

/** Spigot 1.12.2 中史莱姆的 Fluxon 函数别名。 */
@Requires(classes = {"org.bukkit.entity.Slime"})
public final class NovaSlime {

    private NovaSlime() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Slime.class, "size", function -> function
                .returns(Integer.class)
                .invoke(arguments -> slime(arguments).getSize()));
        builder.extension(Slime.class, "setSize", function -> function
                .param("size", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    slime(arguments).setSize(NovaTypeSupport.argument(arguments, 1, Integer.class));
                    return null;
                }));
    }

    private static Slime slime(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Slime.class);
    }
}
