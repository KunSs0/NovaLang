package com.novalang.bukkit.types.event;
import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
@Requires(classes = {"org.bukkit.event.player.PlayerCommandPreprocessEvent"})
public final class NovaPlayerCommandPreprocessEvent {
    private NovaPlayerCommandPreprocessEvent() { }
    public static void register(JavaTypes.Builder b) {
        b.extension(PlayerCommandPreprocessEvent.class, "setPlayer", f -> f.param("player", Player.class).returns(Void.TYPE).invoke(a -> { event(a).setPlayer(NovaTypeSupport.argument(a, 1, Player.class)); return null; }));
    }
    private static PlayerCommandPreprocessEvent event(Object[] a) { return NovaTypeSupport.argument(a, 0, PlayerCommandPreprocessEvent.class); }
}
