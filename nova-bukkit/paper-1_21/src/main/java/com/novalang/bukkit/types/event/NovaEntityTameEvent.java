package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTameEvent;

@Requires(classes = {"org.bukkit.event.entity.EntityTameEvent"})
public final class NovaEntityTameEvent {
    private NovaEntityTameEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityTameEvent.class, "entity", function -> function.returns(Entity.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(EntityTameEvent.class, "owner", function -> function.returns(JavaTypeRef.javaType(AnimalTamer.class).nullable()).invoke(arguments -> event(arguments).getOwner()));
    }

    private static EntityTameEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityTameEvent.class);
    }
}
