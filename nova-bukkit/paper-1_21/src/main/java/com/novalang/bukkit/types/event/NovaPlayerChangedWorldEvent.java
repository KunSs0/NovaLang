package com.novalang.bukkit.types.event;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.World;
import org.bukkit.event.player.PlayerChangedWorldEvent;
@Requires(classes = {"org.bukkit.event.player.PlayerChangedWorldEvent"})
public final class NovaPlayerChangedWorldEvent {
    private NovaPlayerChangedWorldEvent() { }
    public static void register(JavaTypes.Builder b) { b.extension(PlayerChangedWorldEvent.class, "from", f -> f.returns(World.class).invoke(a -> NovaTypeSupport.argument(a, 0, PlayerChangedWorldEvent.class).getFrom())); }
}
