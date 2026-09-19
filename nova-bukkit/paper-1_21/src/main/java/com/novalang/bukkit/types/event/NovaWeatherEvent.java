package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.World;
import org.bukkit.event.weather.WeatherEvent;

/** 天气事件基础类型的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.weather.WeatherEvent"})
public final class NovaWeatherEvent {

    private NovaWeatherEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(WeatherEvent.class, "world", function -> function
                .returns(World.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, WeatherEvent.class).getWorld()));
    }
}
