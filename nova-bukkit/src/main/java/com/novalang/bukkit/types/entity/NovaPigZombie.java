package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.PigZombie;

/** Spigot 1.12.2 PigZombie 扩展。 */
@Requires(classes = {"org.bukkit.entity.PigZombie"})
public final class NovaPigZombie {

    private NovaPigZombie() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PigZombie.class, "anger", function -> function.returns(Integer.class).invoke(arguments -> zombie(arguments).getAnger()));
        builder.extension(PigZombie.class, "setAnger", function -> function.param("anger", Integer.class).returns(Void.TYPE).invoke(arguments -> {
            zombie(arguments).setAnger(NovaTypeSupport.argument(arguments, 1, Integer.class));
            return null;
        }));
        builder.extension(PigZombie.class, "setAngry", function -> function.param("angry", Boolean.class).returns(Void.TYPE).invoke(arguments -> {
            zombie(arguments).setAngry(NovaTypeSupport.argument(arguments, 1, Boolean.class));
            return null;
        }));
        builder.extension(PigZombie.class, "isAngry", function -> function.returns(Boolean.class).invoke(arguments -> zombie(arguments).isAngry()));
    }

    private static PigZombie zombie(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PigZombie.class);
    }
}
