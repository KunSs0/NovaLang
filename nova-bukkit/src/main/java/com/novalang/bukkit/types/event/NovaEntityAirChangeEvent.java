package com.novalang.bukkit.types.event;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.entity.EntityAirChangeEvent;

/** Spigot 1.12.2 实体氧气变化事件别名。 */
public final class NovaEntityAirChangeEvent {

    private NovaEntityAirChangeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityAirChangeEvent.class, "amount", function -> function
                .returns(Integer.class).invoke(arguments -> event(arguments).getAmount()));
        builder.extension(EntityAirChangeEvent.class, "setAmount", function -> function
                .param("amount", Integer.class).returns(Void.TYPE).invoke(arguments -> {
                    event(arguments).setAmount(argument(arguments, 1, Integer.class));
                    return null;
                }));
    }

    private static EntityAirChangeEvent event(Object[] arguments) {
        return argument(arguments, 0, EntityAirChangeEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
