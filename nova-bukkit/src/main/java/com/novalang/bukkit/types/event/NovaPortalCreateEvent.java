package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.event.world.PortalCreateEvent;

@Requires(classes = {"org.bukkit.event.world.PortalCreateEvent"})
public final class NovaPortalCreateEvent {

    private NovaPortalCreateEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PortalCreateEvent.class, "blocks", function -> function
                .returns(JavaTypeRef.listOf(JavaTypeRef.javaType(Block.class)))
                .invoke(arguments -> event(arguments).getBlocks()));
        builder.extension(PortalCreateEvent.class, "reason", function -> function
                .returns(PortalCreateEvent.CreateReason.class)
                .invoke(arguments -> event(arguments).getReason()));
    }

    private static PortalCreateEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PortalCreateEvent.class);
    }
}
