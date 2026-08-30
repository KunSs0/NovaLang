package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.entity.EnderSignal;

/** Spigot 1.12.2 末影之眼信号实体的 Fluxon 函数别名。 */
public final class NovaEnderSignal {

    private NovaEnderSignal() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EnderSignal.class, "targetLocation", function -> function.returns(Location.class)
                .invoke(arguments -> signal(arguments).getTargetLocation()));
        builder.extension(EnderSignal.class, "setTargetLocation", function -> function.param("location", Location.class).returns(Void.TYPE).invoke(arguments -> {
            signal(arguments).setTargetLocation(argument(arguments, 1, Location.class));
            return null;
        }));
        builder.extension(EnderSignal.class, "dropItem", function -> function.returns(Boolean.class)
                .invoke(arguments -> signal(arguments).getDropItem()));
        builder.extension(EnderSignal.class, "setDropItem", function -> function.param("drop", Boolean.class).returns(Void.TYPE).invoke(arguments -> {
            signal(arguments).setDropItem(argument(arguments, 1, Boolean.class));
            return null;
        }));
        builder.extension(EnderSignal.class, "despawnTimer", function -> function.returns(Integer.class)
                .invoke(arguments -> signal(arguments).getDespawnTimer()));
        builder.extension(EnderSignal.class, "setDespawnTimer", function -> function.param("ticks", Integer.class).returns(Void.TYPE).invoke(arguments -> {
            signal(arguments).setDespawnTimer(argument(arguments, 1, Integer.class));
            return null;
        }));
    }

    private static EnderSignal signal(Object[] arguments) {
        return argument(arguments, 0, EnderSignal.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
