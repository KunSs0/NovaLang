package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockIgniteEvent;

/** 方块点燃事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.block.BlockIgniteEvent"})
public final class NovaBlockIgniteEvent {

    private NovaBlockIgniteEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BlockIgniteEvent.class, "cause", function -> function
                .returns(BlockIgniteEvent.IgniteCause.class)
                .invoke(arguments -> event(arguments).getCause()));
        builder.extension(BlockIgniteEvent.class, "player", function -> function
                .returns(JavaTypeRef.javaType(Player.class).nullable())
                .invoke(arguments -> event(arguments).getPlayer()));
        builder.extension(BlockIgniteEvent.class, "ignitingEntity", function -> function
                .returns(JavaTypeRef.javaType(Entity.class).nullable())
                .invoke(arguments -> event(arguments).getIgnitingEntity()));
        builder.extension(BlockIgniteEvent.class, "ignitingBlock", function -> function
                .returns(JavaTypeRef.javaType(Block.class).nullable())
                .invoke(arguments -> event(arguments).getIgnitingBlock()));
    }

    private static BlockIgniteEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, BlockIgniteEvent.class);
    }
}
