package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

/** Spigot 1.12.2 EntityDeathEvent 击杀者别名。 */
@Requires(classes = {"org.bukkit.event.entity.EntityDeathEvent"})
public final class NovaEntityDeathKiller {

    private NovaEntityDeathKiller() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityDeathEvent.class, "killer", function -> function.returns(JavaTypeRef.javaType(Player.class).nullable()).invoke(arguments -> event(arguments).getEntity().getKiller()));
    }

    private static EntityDeathEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, EntityDeathEvent.class);
    }
}
