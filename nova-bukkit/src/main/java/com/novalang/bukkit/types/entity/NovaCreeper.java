package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Creeper;

/** Creeper 在 Spigot 1.12.2 可用的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.entity.Creeper"})
final class NovaCreeper {

    private NovaCreeper() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Creeper.class, "isPowered", function -> function.returns(Boolean.class)
                .invoke(arguments -> creeper(arguments).isPowered()));
        builder.extension(Creeper.class, "setPowered", function -> function.param("powered", Boolean.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    creeper(arguments).setPowered(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(Creeper.class, "maxFuseTicks", function -> function.returns(Integer.class)
                .invoke(arguments -> creeper(arguments).getMaxFuseTicks()));
        builder.extension(Creeper.class, "setMaxFuseTicks", function -> function.param("ticks", Integer.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    creeper(arguments).setMaxFuseTicks(NovaTypeSupport.argument(arguments, 1, Integer.class));
                    return null;
                }));
        builder.extension(Creeper.class, "explosionRadius", function -> function.returns(Integer.class)
                .invoke(arguments -> creeper(arguments).getExplosionRadius()));
        builder.extension(Creeper.class, "setExplosionRadius", function -> function.param("radius", Integer.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    creeper(arguments).setExplosionRadius(NovaTypeSupport.argument(arguments, 1, Integer.class));
                    return null;
                }));
    }

    private static Creeper creeper(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Creeper.class);
    }
}
