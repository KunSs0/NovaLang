package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileLaunchEvent;

/** 投射物发射事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.entity.ProjectileLaunchEvent"})
public final class NovaProjectileLaunchEvent {

    private NovaProjectileLaunchEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ProjectileLaunchEvent.class, "entity", function -> function
                .returns(Projectile.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, ProjectileLaunchEvent.class).getEntity()));
    }
}
