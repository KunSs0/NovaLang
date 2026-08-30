package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.MerchantRecipe;

@Requires(classes = {"org.bukkit.event.entity.VillagerAcquireTradeEvent"})
public final class NovaVillagerAcquireTradeEvent {
    private NovaVillagerAcquireTradeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(VillagerAcquireTradeEvent.class, "entity", function -> function.returns(Entity.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(VillagerAcquireTradeEvent.class, "recipe", function -> function.returns(MerchantRecipe.class).invoke(arguments -> event(arguments).getRecipe()));
        builder.extension(VillagerAcquireTradeEvent.class, "setRecipe", function -> function.param("recipe", MerchantRecipe.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setRecipe(NovaTypeSupport.argument(arguments, 1, MerchantRecipe.class)); return null; }));
    }

    private static VillagerAcquireTradeEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, VillagerAcquireTradeEvent.class);
    }
}
