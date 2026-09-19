package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.PortalType;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityCreatePortalEvent;

@Requires(classes = {"org.bukkit.event.entity.EntityCreatePortalEvent"})
public final class NovaEntityCreatePortalEvent {
    private NovaEntityCreatePortalEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityCreatePortalEvent.class, "entity", function -> function.returns(Entity.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(EntityCreatePortalEvent.class, "blocks", function -> function.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Block.class))).invoke(arguments -> event(arguments).getBlocks()));
        builder.extension(EntityCreatePortalEvent.class, "portalType", function -> function.returns(PortalType.class).invoke(arguments -> event(arguments).getPortalType()));
    }

    private static EntityCreatePortalEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityCreatePortalEvent.class);
    }
}
