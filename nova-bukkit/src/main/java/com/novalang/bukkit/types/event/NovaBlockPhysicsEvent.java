package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPhysicsEvent;

/** 方块物理更新事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockPhysicsEvent"})
@SuppressWarnings("deprecation")
public final class NovaBlockPhysicsEvent {

    private NovaBlockPhysicsEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockPhysicsEvent.class, "changedTypeId", function -> function
                .returns(Integer.class)
                .invoke(arguments -> event(arguments).getChangedTypeId()));
        builder.extension(BlockPhysicsEvent.class, "changedType", function -> function
                .returns(Material.class)
                .invoke(arguments -> event(arguments).getChangedType()));
    }

    private static BlockPhysicsEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockPhysicsEvent.class);
    }
}
