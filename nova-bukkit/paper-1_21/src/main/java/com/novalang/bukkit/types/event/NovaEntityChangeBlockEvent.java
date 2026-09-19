package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityChangeBlockEvent;

@Requires(classes = {"org.bukkit.event.entity.EntityChangeBlockEvent"})
public final class NovaEntityChangeBlockEvent {
    private NovaEntityChangeBlockEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityChangeBlockEvent.class, "entity", function -> function.returns(Entity.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(EntityChangeBlockEvent.class, "block", function -> function.returns(Block.class).invoke(arguments -> event(arguments).getBlock()));
        builder.extension(EntityChangeBlockEvent.class, "to", function -> function.returns(Material.class).invoke(arguments -> event(arguments).getTo()));
    }

    private static EntityChangeBlockEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityChangeBlockEvent.class);
    }
}
