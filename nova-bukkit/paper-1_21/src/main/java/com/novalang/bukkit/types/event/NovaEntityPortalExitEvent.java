package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.util.Vector;

@Requires(classes = {"org.bukkit.event.entity.EntityPortalExitEvent"})
public final class NovaEntityPortalExitEvent {
    private NovaEntityPortalExitEvent() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityPortalExitEvent.class, "entity", function -> function.returns(Entity.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(EntityPortalExitEvent.class, "before", function -> function.returns(Vector.class).invoke(arguments -> event(arguments).getBefore()));
        builder.extension(EntityPortalExitEvent.class, "after", function -> function.returns(Vector.class).invoke(arguments -> event(arguments).getAfter()));
        builder.extension(EntityPortalExitEvent.class, "setAfter", function -> function.param("after", Vector.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setAfter(NovaTypeSupport.argument(arguments, 1, Vector.class)); return null; }));
    }
    private static EntityPortalExitEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityPortalExitEvent.class);
    }
}
