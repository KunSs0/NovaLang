package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

/** 载具销毁事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.vehicle.VehicleDestroyEvent"})
public final class NovaVehicleDestroyEvent {

    private NovaVehicleDestroyEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(VehicleDestroyEvent.class, "attacker", function -> function
                .returns(JavaTypeRef.javaType(Entity.class).nullable())
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, VehicleDestroyEvent.class).getAttacker()));
    }
}
