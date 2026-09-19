package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.entity.EntityUnleashEvent;

@Requires(classes = {"org.bukkit.event.entity.EntityUnleashEvent"})
public final class NovaEntityUnleashEvent {
    private NovaEntityUnleashEvent() {
    }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityUnleashEvent.class, "reason", function -> function.returns(EntityUnleashEvent.UnleashReason.class).invoke(arguments -> event(arguments).getReason()));
    }
    private static EntityUnleashEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityUnleashEvent.class);
    }
}
