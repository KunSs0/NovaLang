package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.event.entity.LingeringPotionSplashEvent;

/** 滞留药水事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.entity.LingeringPotionSplashEvent"})
public final class NovaLingeringPotionSplashEvent {

    private NovaLingeringPotionSplashEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(LingeringPotionSplashEvent.class, "entity", function -> function
                .returns(LingeringPotion.class)
                .invoke(arguments -> event(arguments).getEntity()));
        builder.extension(LingeringPotionSplashEvent.class, "areaEffectCloud", function -> function
                .returns(AreaEffectCloud.class)
                .invoke(arguments -> event(arguments).getAreaEffectCloud()));
    }

    private static LingeringPotionSplashEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, LingeringPotionSplashEvent.class);
    }
}
