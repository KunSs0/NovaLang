package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.World;
import org.bukkit.event.world.WorldEvent;

@Requires(classes = {"org.bukkit.event.world.WorldEvent"})
public final class NovaWorldEvent {
    private NovaWorldEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(WorldEvent.class, "world", f -> f.returns(World.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, WorldEvent.class).getWorld()));
    }
}
