package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.event.vehicle.VehicleDamageEvent;

/** 载具受损事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.vehicle.VehicleDamageEvent"})
public final class NovaVehicleDamageEvent {

    private NovaVehicleDamageEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(VehicleDamageEvent.class, "attacker", function -> function
                .returns(JavaTypeRef.javaType(Entity.class).nullable())
                .invoke(arguments -> event(arguments).getAttacker()));
        builder.extension(VehicleDamageEvent.class, "damage", function -> function
                .returns(Double.class)
                .invoke(arguments -> event(arguments).getDamage()));
        builder.extension(VehicleDamageEvent.class, "setDamage", function -> function
                .param("damage", Double.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    event(arguments).setDamage(argument(arguments, 1, Double.class));
                    return null;
                }));
    }

    private static VehicleDamageEvent event(Object[] arguments) {
        return argument(arguments, 0, VehicleDamageEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
