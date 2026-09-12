package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.hanging.HangingBreakEvent;

/** 悬挂实体破坏事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.hanging.HangingBreakEvent"})
public final class NovaHangingBreakEvent {

    private NovaHangingBreakEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(HangingBreakEvent.class, "cause", function -> function
                .returns(HangingBreakEvent.RemoveCause.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, HangingBreakEvent.class).getCause()));
    }
}
