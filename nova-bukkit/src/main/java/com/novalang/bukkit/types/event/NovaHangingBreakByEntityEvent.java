package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

/** 由实体破坏悬挂实体事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.hanging.HangingBreakByEntityEvent"})
public final class NovaHangingBreakByEntityEvent {

    private NovaHangingBreakByEntityEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(HangingBreakByEntityEvent.class, "remover", function -> function
                .returns(JavaTypeRef.javaType(Entity.class).nullable())
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, HangingBreakByEntityEvent.class).getRemover()));
    }
}
