package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.SpawnChangeEvent;

@Requires(classes = {"org.bukkit.event.world.SpawnChangeEvent"})
public final class NovaSpawnChangeEvent {
    private NovaSpawnChangeEvent() { }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(SpawnChangeEvent.class, "previousLocation", f -> f.returns(Location.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, SpawnChangeEvent.class).getPreviousLocation()));
        builder.extension(SpawnChangeEvent.class, "handlers", f -> f.returns(HandlerList.class)
                .invoke(a -> NovaTypeSupport.argument(a, 0, SpawnChangeEvent.class).getHandlers()));
        builder.extension(SpawnChangeEvent.class, "handlerList", f -> f.returns(HandlerList.class)
                .invoke(a -> SpawnChangeEvent.getHandlerList()));
    }
}
