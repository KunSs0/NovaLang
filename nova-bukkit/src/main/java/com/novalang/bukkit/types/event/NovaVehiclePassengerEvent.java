package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

/** 载具上下乘客事件的可选 Fluxon 别名。 */
@Requires(classes = {
        "org.bukkit.event.vehicle.VehicleEnterEvent",
        "org.bukkit.event.vehicle.VehicleExitEvent"
})
public final class NovaVehiclePassengerEvent {

    private NovaVehiclePassengerEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(VehicleEnterEvent.class, "entered", function -> function
                .returns(Entity.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, VehicleEnterEvent.class).getEntered()));
        builder.extension(VehicleExitEvent.class, "exited", function -> function
                .returns(LivingEntity.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, VehicleExitEvent.class).getExited()));
    }
}
