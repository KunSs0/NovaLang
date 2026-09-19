package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityTeleportEvent;

/** 非玩家实体传送事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.entity.EntityTeleportEvent"})
public final class NovaEntityTeleportEvent {

    private NovaEntityTeleportEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityTeleportEvent.class, "from", function -> function
                .returns(Location.class)
                .invoke(arguments -> event(arguments).getFrom()));
        builder.extension(EntityTeleportEvent.class, "setFrom", function -> function
                .param("location", Location.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setFrom(argument(arguments, 1, Location.class));
                    return null;
                }));
        builder.extension(EntityTeleportEvent.class, "to", function -> function
                .returns(Location.class)
                .invoke(arguments -> event(arguments).getTo()));
        builder.extension(EntityTeleportEvent.class, "setTo", function -> function
                .param("location", Location.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setTo(argument(arguments, 1, Location.class));
                    return null;
                }));
    }

    private static EntityTeleportEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityTeleportEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
