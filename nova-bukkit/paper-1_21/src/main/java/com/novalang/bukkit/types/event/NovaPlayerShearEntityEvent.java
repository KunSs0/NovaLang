package com.novalang.bukkit.types.event;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerShearEntityEvent;

@Requires(classes = {"org.bukkit.event.player.PlayerShearEntityEvent"})
public final class NovaPlayerShearEntityEvent {

    private NovaPlayerShearEntityEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerShearEntityEvent.class, "entity", function -> function.returns(Entity.class).invoke(arguments -> event(arguments).getEntity()));
    }

    private static PlayerShearEntityEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerShearEntityEvent.class);
    }
}
