package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityCombustByBlockEvent;

@Requires(classes = {"org.bukkit.event.entity.EntityCombustByBlockEvent"})
public final class NovaEntityCombustByBlockEvent {
    private NovaEntityCombustByBlockEvent() {
    }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityCombustByBlockEvent.class, "combuster", function -> function.returns(JavaTypeRef.javaType(Block.class).nullable()).invoke(arguments -> event(arguments).getCombuster()));
    }
    private static EntityCombustByBlockEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityCombustByBlockEvent.class);
    }
}
