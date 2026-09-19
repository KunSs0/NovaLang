package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntitySpawnEvent;

@Requires(classes = {"org.bukkit.event.entity.EntitySpawnEvent"})
public final class NovaEntitySpawnEvent {
    private NovaEntitySpawnEvent() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntitySpawnEvent.class, "entity", function -> function.returns(Entity.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(EntitySpawnEvent.class, "location", function -> function.returns(Location.class).invoke(arguments -> event(arguments).getLocation()));
    }
    private static EntitySpawnEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntitySpawnEvent.class);
    }
}
