package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.entity.ExplosionPrimeEvent;

/** 爆炸预处理事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.entity.ExplosionPrimeEvent"})
public final class NovaExplosionPrimeEvent {

    private NovaExplosionPrimeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ExplosionPrimeEvent.class, "radius", function -> function
                .returns(Float.class)
                .invoke(arguments -> event(arguments).getRadius()));
        builder.extension(ExplosionPrimeEvent.class, "setRadius", function -> function
                .param("radius", Float.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setRadius(argument(arguments, 1, Float.class));
                    return null;
                }));
        builder.extension(ExplosionPrimeEvent.class, "fire", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> event(arguments).getFire()));
        builder.extension(ExplosionPrimeEvent.class, "setFire", function -> function
                .param("fire", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setFire(argument(arguments, 1, Boolean.class));
                    return null;
                }));
    }

    private static ExplosionPrimeEvent event(Object[] arguments) {
        return argument(arguments, 0, ExplosionPrimeEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
