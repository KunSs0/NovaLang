package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.event.hanging.HangingEvent;

/** 悬挂实体事件基础类型的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.hanging.HangingEvent"})
public final class NovaHangingEvent {

    private NovaHangingEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(HangingEvent.class, "entity", function -> function
                .returns(Entity.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, HangingEvent.class).getEntity()));
    }
}
