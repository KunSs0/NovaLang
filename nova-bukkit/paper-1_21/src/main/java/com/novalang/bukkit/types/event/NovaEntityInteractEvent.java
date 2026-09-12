package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityInteractEvent;

@Requires(classes = {"org.bukkit.event.entity.EntityInteractEvent"})
public final class NovaEntityInteractEvent {
    private NovaEntityInteractEvent() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityInteractEvent.class, "block", function -> function.returns(Block.class).invoke(arguments -> event(arguments).getBlock()));
    }
    private static EntityInteractEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityInteractEvent.class);
    }
}
