package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.LightningStrike;
import org.bukkit.event.weather.LightningStrikeEvent;

/** 闪电生成事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.weather.LightningStrikeEvent"})
public final class NovaLightningStrikeEvent {

    private NovaLightningStrikeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(LightningStrikeEvent.class, "lightning", function -> function
                .returns(LightningStrike.class)
                .invoke(arguments -> event(arguments).getLightning()));
    }

    private static LightningStrikeEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, LightningStrikeEvent.class);
    }
}
