package com.novalang.bukkit.types.event;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityPortalEnterEvent;

/** Spigot 1.12.2 实体进入传送门事件别名。 */
public final class NovaEntityPortalEnterEvent {

    private NovaEntityPortalEnterEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityPortalEnterEvent.class, "location", function -> function.returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, EntityPortalEnterEvent.class).getLocation()));
    }
}
