package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.PotionSplashEvent;

import java.util.Collection;

/** 药水泼溅事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.entity.PotionSplashEvent"})
public final class NovaPotionSplashEvent {

    private NovaPotionSplashEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PotionSplashEvent.class, "entity", function -> function
                .returns(ThrownPotion.class)
                .invoke(arguments -> event(arguments).getEntity()));
        builder.extension(PotionSplashEvent.class, "potion", function -> function
                .returns(ThrownPotion.class)
                .invoke(arguments -> event(arguments).getPotion()));
        builder.extension(PotionSplashEvent.class, "affectedEntities", function -> function
                .returns(JavaTypeRef.javaType(Collection.class))
                .invoke(arguments -> event(arguments).getAffectedEntities()));
        builder.extension(PotionSplashEvent.class, "getIntensity", function -> function
                .param("entity", LivingEntity.class)
                .returns(Double.class)
                .invoke(arguments -> event(arguments).getIntensity(argument(arguments, 1, LivingEntity.class))));
        builder.extension(PotionSplashEvent.class, "setIntensity", function -> function
                .param("entity", LivingEntity.class)
                .param("intensity", Double.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setIntensity(
                            argument(arguments, 1, LivingEntity.class),
                            argument(arguments, 2, Double.class));
                    return null;
                }));
    }

    private static PotionSplashEvent event(Object[] arguments) {
        return argument(arguments, 0, PotionSplashEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
