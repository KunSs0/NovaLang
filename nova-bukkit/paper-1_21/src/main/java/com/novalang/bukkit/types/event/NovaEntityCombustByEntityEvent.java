package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityCombustByEntityEvent;

@Requires(classes = {"org.bukkit.event.entity.EntityCombustByEntityEvent"})
public final class NovaEntityCombustByEntityEvent {
    private NovaEntityCombustByEntityEvent() {
    }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityCombustByEntityEvent.class, "combuster", function -> function.returns(JavaTypeRef.javaType(Entity.class).nullable()).invoke(arguments -> event(arguments).getCombuster()));
    }
    private static EntityCombustByEntityEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityCombustByEntityEvent.class);
    }
}
