package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Firework;
import org.bukkit.event.entity.FireworkExplodeEvent;

@Requires(classes = {"org.bukkit.event.entity.FireworkExplodeEvent"})
public final class NovaFireworkExplodeEvent {
    private NovaFireworkExplodeEvent() {
    }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(FireworkExplodeEvent.class, "entity", function -> function.returns(Firework.class).invoke(arguments -> event(arguments).getEntity()));
    }
    private static FireworkExplodeEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, FireworkExplodeEvent.class);
    }
}
