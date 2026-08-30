package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

@Requires(classes = {"org.bukkit.event.player.PlayerResourcePackStatusEvent"})
public final class NovaPlayerResourcePackStatusEvent {

    private NovaPlayerResourcePackStatusEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerResourcePackStatusEvent.class, "status", function -> function
                .returns(PlayerResourcePackStatusEvent.Status.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, PlayerResourcePackStatusEvent.class).getStatus()));
    }
}
