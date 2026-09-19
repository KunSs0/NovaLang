package com.novalang.bukkit.types.event;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.event.entity.HorseJumpEvent;

/** Spigot 1.12.2 马匹跳跃事件别名。 */
public final class NovaHorseJumpEvent {

    private NovaHorseJumpEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(HorseJumpEvent.class, "entity", function -> function
                .returns(AbstractHorse.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(HorseJumpEvent.class, "power", function -> function
                .returns(Float.class).invoke(arguments -> event(arguments).getPower()));
        builder.extension(HorseJumpEvent.class, "setPower", function -> function
                .param("power", Float.class).returns(Void.TYPE).invoke(arguments -> {
                    event(arguments).setPower(argument(arguments, 1, Float.class));
                    return null;
                }));
    }

    private static HorseJumpEvent event(Object[] arguments) {
        return argument(arguments, 0, HorseJumpEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
