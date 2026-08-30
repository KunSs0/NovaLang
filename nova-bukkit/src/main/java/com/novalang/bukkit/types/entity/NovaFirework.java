package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

/** Spigot 1.12.2 Firework 实体扩展。 */
@Requires(classes = {"org.bukkit.entity.Firework"})
public final class NovaFirework {
    private NovaFirework() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Firework.class, "fireworkMeta", function -> function
                .returns(FireworkMeta.class)
                .invoke(arguments -> firework(arguments).getFireworkMeta()));
        builder.extension(Firework.class, "setFireworkMeta", function -> function
                .param("meta", FireworkMeta.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    firework(arguments).setFireworkMeta(NovaTypeSupport.argument(arguments, 1, FireworkMeta.class));
                    return null;
                }));
        builder.extension(Firework.class, "detonate", function -> function
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    firework(arguments).detonate();
                    return null;
                }));
    }

    private static Firework firework(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Firework.class);
    }
}
