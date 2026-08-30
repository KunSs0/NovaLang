package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.entity.PigZapEvent;

@Requires(classes = {"org.bukkit.event.entity.PigZapEvent"})
public final class NovaPigZapEvent {
    private NovaPigZapEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PigZapEvent.class, "entity", function -> function.returns(Entity.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(PigZapEvent.class, "lightning", function -> function.returns(LightningStrike.class).invoke(arguments -> event(arguments).getLightning()));
        builder.extension(PigZapEvent.class, "pigZombie", function -> function.returns(PigZombie.class).invoke(arguments -> event(arguments).getPigZombie()));
    }

    private static PigZapEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PigZapEvent.class);
    }
}
