package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.DyeColor;
import org.bukkit.entity.Sheep;
import org.bukkit.event.entity.SheepDyeWoolEvent;

@Requires(classes = {"org.bukkit.event.entity.SheepDyeWoolEvent"})
public final class NovaSheepDyeWoolEvent {
    private NovaSheepDyeWoolEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(SheepDyeWoolEvent.class, "entity", function -> function.returns(Sheep.class).invoke(arguments -> event(arguments).getEntity()));
        builder.extension(SheepDyeWoolEvent.class, "color", function -> function.returns(DyeColor.class).invoke(arguments -> event(arguments).getColor()));
        builder.extension(SheepDyeWoolEvent.class, "setColor", function -> function.param("color", DyeColor.class).returns(Void.TYPE).invoke(arguments -> { event(arguments).setColor(NovaTypeSupport.argument(arguments, 1, DyeColor.class)); return null; }));
    }

    private static SheepDyeWoolEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, SheepDyeWoolEvent.class);
    }
}
