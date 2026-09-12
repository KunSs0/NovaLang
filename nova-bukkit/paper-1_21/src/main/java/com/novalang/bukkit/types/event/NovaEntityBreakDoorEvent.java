package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityBreakDoorEvent;

@Requires(classes = {"org.bukkit.event.entity.EntityBreakDoorEvent"})
public final class NovaEntityBreakDoorEvent {
    private NovaEntityBreakDoorEvent() {
    }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityBreakDoorEvent.class, "entity", function -> function.returns(Entity.class).invoke(arguments -> event(arguments).getEntity()));
    }
    private static EntityBreakDoorEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityBreakDoorEvent.class);
    }
}
