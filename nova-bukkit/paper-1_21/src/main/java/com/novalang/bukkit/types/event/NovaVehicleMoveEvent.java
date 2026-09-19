package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.event.vehicle.VehicleMoveEvent;

/** 载具移动事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.vehicle.VehicleMoveEvent"})
public final class NovaVehicleMoveEvent {

    private NovaVehicleMoveEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(VehicleMoveEvent.class, "from", function -> function
                .returns(Location.class)
                .invoke(arguments -> event(arguments).getFrom()));
        builder.extension(VehicleMoveEvent.class, "to", function -> function
                .returns(Location.class)
                .invoke(arguments -> event(arguments).getTo()));
    }

    private static VehicleMoveEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, VehicleMoveEvent.class);
    }
}
