package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.SlimeSplitEvent;

@Requires(classes = {"org.bukkit.event.entity.SlimeSplitEvent"})
public final class NovaSlimeSplitEvent {
    private NovaSlimeSplitEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(SlimeSplitEvent.class, "entity", function -> function.returns(Slime.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(SlimeSplitEvent.class, "count", function -> function.returns(Integer.class).invoke(arguments -> event(arguments).getCount()));
        builder.extension(SlimeSplitEvent.class, "setCount", function -> function.param("count", Integer.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setCount(NovaTypeSupport.argument(arguments, 1, Integer.class)); return null; }));
    }

    private static SlimeSplitEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, SlimeSplitEvent.class);
    }
}
