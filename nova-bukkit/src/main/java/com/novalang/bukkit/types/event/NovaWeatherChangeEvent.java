package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.weather.WeatherChangeEvent;

/** 天气变化事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.weather.WeatherChangeEvent"})
public final class NovaWeatherChangeEvent {

    private NovaWeatherChangeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(WeatherChangeEvent.class, "toWeatherState", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, WeatherChangeEvent.class).toWeatherState()));
    }
}
