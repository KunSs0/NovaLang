package com.novalang.bukkit.types.event;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.event.entity.ItemDespawnEvent;

/** Spigot 1.12.2 物品消失事件别名。 */
public final class NovaItemDespawnEvent {

    private NovaItemDespawnEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ItemDespawnEvent.class, "location", function -> function.returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, ItemDespawnEvent.class).getLocation()));
    }
}
