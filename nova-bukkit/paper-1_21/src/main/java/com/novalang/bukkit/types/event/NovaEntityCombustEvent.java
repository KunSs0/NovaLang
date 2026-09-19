package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.entity.EntityCombustEvent;

/** 实体燃烧事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.entity.EntityCombustEvent"})
public final class NovaEntityCombustEvent {

    private NovaEntityCombustEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityCombustEvent.class, "duration", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getDuration()));
        builder.extension(EntityCombustEvent.class, "setDuration", function -> function
                .param("duration", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setDuration(argument(arguments, 1, Integer.class));
                    return null;
                }));
    }

    private static EntityCombustEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityCombustEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
