package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LightningStrike;
import org.bukkit.event.entity.CreeperPowerEvent;

@Requires(classes = {"org.bukkit.event.entity.CreeperPowerEvent"})
public final class NovaCreeperPowerEvent {
    private NovaCreeperPowerEvent() {
    }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(CreeperPowerEvent.class, "entity", function -> function.returns(Creeper.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(CreeperPowerEvent.class, "lightning", function -> function.returns(LightningStrike.class).invoke(arguments -> event(arguments).getLightning()));
        builder.extension(CreeperPowerEvent.class, "cause", function -> function.returns(CreeperPowerEvent.PowerCause.class).invoke(arguments -> event(arguments).getCause()));
    }
    private static CreeperPowerEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, CreeperPowerEvent.class);
    }
}
