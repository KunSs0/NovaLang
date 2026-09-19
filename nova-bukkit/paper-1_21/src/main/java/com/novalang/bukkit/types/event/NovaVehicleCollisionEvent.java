package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;

/** 载具实体与方块碰撞事件的可选 Fluxon 别名。 */
@Requires(classes = {
        "org.bukkit.event.vehicle.VehicleEntityCollisionEvent",
        "org.bukkit.event.vehicle.VehicleBlockCollisionEvent"
})
public final class NovaVehicleCollisionEvent {

    private NovaVehicleCollisionEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(VehicleEntityCollisionEvent.class, "entity", function -> function
                .returns(Entity.class)
                .invoke(arguments -> entityCollision(arguments).getEntity()));
        builder.extension(VehicleEntityCollisionEvent.class, "isPickupCancelled", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> entityCollision(arguments).isPickupCancelled()));
        builder.extension(VehicleEntityCollisionEvent.class, "setPickupCancelled", function -> function
                .param("cancelled", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    entityCollision(arguments).setPickupCancelled(argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(VehicleEntityCollisionEvent.class, "isCollisionCancelled", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> entityCollision(arguments).isCollisionCancelled()));
        builder.extension(VehicleEntityCollisionEvent.class, "setCollisionCancelled", function -> function
                .param("cancelled", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    entityCollision(arguments).setCollisionCancelled(argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(VehicleBlockCollisionEvent.class, "block", function -> function
                .returns(Block.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, VehicleBlockCollisionEvent.class).getBlock()));
    }

    private static VehicleEntityCollisionEvent entityCollision(Object[] arguments) {
        return argument(arguments, 0, VehicleEntityCollisionEvent.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
