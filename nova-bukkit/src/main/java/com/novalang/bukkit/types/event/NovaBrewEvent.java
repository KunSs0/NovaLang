package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;

/** 酿造完成事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.inventory.BrewEvent"})
public final class NovaBrewEvent {

    private NovaBrewEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(BrewEvent.class, "contents", function -> function
                .returns(BrewerInventory.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, BrewEvent.class).getContents()));
        builder.extension(BrewEvent.class, "fuelLevel", function -> function
                .returns(Integer.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, BrewEvent.class).getFuelLevel()));
    }
}
