package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.StructureGrowEvent;

@Requires(classes = {"org.bukkit.event.world.StructureGrowEvent"})
public final class NovaStructureGrowEvent {
    private NovaStructureGrowEvent() { }
    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullablePlayer = JavaTypeRef.javaType(Player.class).nullable();
        builder.extension(StructureGrowEvent.class, "location", f -> f.returns(Location.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, StructureGrowEvent.class).getLocation()));
        builder.extension(StructureGrowEvent.class, "species", f -> f.returns(TreeType.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, StructureGrowEvent.class).getSpecies()));
        builder.extension(StructureGrowEvent.class, "isFromBonemeal", f -> f.returns(Boolean.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, StructureGrowEvent.class).isFromBonemeal()));
        builder.extension(StructureGrowEvent.class, "player", f -> f.returns(nullablePlayer)
                .invoke(a -> NovaTypeSupport.argument(a, 0, StructureGrowEvent.class).getPlayer()));
        builder.extension(StructureGrowEvent.class, "blocks", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(org.bukkit.block.BlockState.class)))
                .invoke(a -> NovaTypeSupport.argument(a, 0, StructureGrowEvent.class).getBlocks()));
        builder.extension(StructureGrowEvent.class, "handlers", f -> f.returns(HandlerList.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, StructureGrowEvent.class).getHandlers()));
        builder.extension(StructureGrowEvent.class, "handlerList", f -> f.returns(HandlerList.class)
                .invoke(a -> StructureGrowEvent.getHandlerList()));
    }
}
