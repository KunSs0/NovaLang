package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Sheep;
import org.bukkit.event.entity.SheepRegrowWoolEvent;

@Requires(classes = {"org.bukkit.event.entity.SheepRegrowWoolEvent"})
public final class NovaSheepRegrowWoolEvent {
    private NovaSheepRegrowWoolEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(SheepRegrowWoolEvent.class, "entity", function -> function.returns(Sheep.class).invoke(arguments -> event(arguments).getEntity()));
    }

    private static SheepRegrowWoolEvent event(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, SheepRegrowWoolEvent.class);
    }
}
