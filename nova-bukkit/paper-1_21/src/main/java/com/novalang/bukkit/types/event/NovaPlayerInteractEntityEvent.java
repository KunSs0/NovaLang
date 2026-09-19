package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

/** 玩家右键实体事件的可选编译期别名。 */
@Requires(classes = {"org.bukkit.event.player.PlayerInteractEntityEvent"})
public final class NovaPlayerInteractEntityEvent {

    private NovaPlayerInteractEntityEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PlayerInteractEntityEvent.class, "rightClicked", function -> function
                .returns(Entity.class)
                .invoke(arguments -> event(arguments).getRightClicked()));
        builder.extension(PlayerInteractEntityEvent.class, "hand", function -> function
                .returns(EquipmentSlot.class)
                .invoke(arguments -> event(arguments).getHand()));
    }

    private static PlayerInteractEntityEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PlayerInteractEntityEvent.class);
    }
}
