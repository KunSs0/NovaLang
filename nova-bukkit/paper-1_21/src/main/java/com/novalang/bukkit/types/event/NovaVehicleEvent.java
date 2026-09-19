package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleEvent;

/** 载具事件基础类型的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.vehicle.VehicleEvent"})
public final class NovaVehicleEvent {

    private NovaVehicleEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(VehicleEvent.class, "vehicle", function -> function
                .returns(Vehicle.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, VehicleEvent.class).getVehicle()));
    }
}
