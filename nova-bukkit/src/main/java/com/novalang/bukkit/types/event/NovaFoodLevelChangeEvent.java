package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.FoodLevelChangeEvent;

@Requires(classes = {"org.bukkit.event.entity.FoodLevelChangeEvent"})
public final class NovaFoodLevelChangeEvent {
    private NovaFoodLevelChangeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(FoodLevelChangeEvent.class, "entity", function -> function.returns(HumanEntity.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(FoodLevelChangeEvent.class, "foodLevel", function -> function.returns(Integer.class).invoke(arguments -> event(arguments).getFoodLevel()));
        builder.extension(FoodLevelChangeEvent.class, "setFoodLevel", function -> function.param("foodLevel", Integer.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setFoodLevel(NovaTypeSupport.argument(arguments, 1, Integer.class)); return null; }));
    }

    private static FoodLevelChangeEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, FoodLevelChangeEvent.class);
    }
}
