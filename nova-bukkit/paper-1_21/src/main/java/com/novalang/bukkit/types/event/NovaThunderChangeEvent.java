package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.weather.ThunderChangeEvent;

/** 雷暴变化事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.weather.ThunderChangeEvent"})
public final class NovaThunderChangeEvent {

    private NovaThunderChangeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ThunderChangeEvent.class, "toThunderState", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, ThunderChangeEvent.class).toThunderState()));
    }
}
