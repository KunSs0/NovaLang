package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.ExpBottleEvent;

@Requires(classes = {"org.bukkit.event.entity.ExpBottleEvent"})
public final class NovaExpBottleEvent {
    private NovaExpBottleEvent() {
    }
    public static void register(JavaTypes.Builder builder) {
        builder.extension(ExpBottleEvent.class, "entity", function -> function.returns(Entity.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(ExpBottleEvent.class, "showEffect", function -> function.returns(Boolean.class).invoke(arguments -> event(arguments).getShowEffect()));
        builder.extension(ExpBottleEvent.class, "setShowEffect", function -> function.param("showEffect", Boolean.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setShowEffect(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(ExpBottleEvent.class, "experience", function -> function.returns(Integer.class).invoke(arguments -> event(arguments).getExperience()));
        builder.extension(ExpBottleEvent.class, "setExperience", function -> function.param("experience", Integer.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setExperience(NovaTypeSupport.argument(arguments, 1, Integer.class)); return null; }));
    }
    private static ExpBottleEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ExpBottleEvent.class);
    }
}
