package com.novalang.bukkit.types.event;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.entity.EntityToggleGlideEvent;

/** Spigot 1.12.2 实体滑翔状态事件别名。 */
public final class NovaEntityToggleGlideEvent {

    private NovaEntityToggleGlideEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(EntityToggleGlideEvent.class, "isGliding", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, EntityToggleGlideEvent.class).isGliding()));
    }
}
