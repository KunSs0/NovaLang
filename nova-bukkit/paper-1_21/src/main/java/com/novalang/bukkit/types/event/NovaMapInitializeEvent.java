package com.novalang.bukkit.types.event;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapView;

/** 地图初始化事件的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.event.server.MapInitializeEvent"})
public final class NovaMapInitializeEvent {

    private NovaMapInitializeEvent() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(MapInitializeEvent.class, "map", function -> function
                .returns(MapView.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, MapInitializeEvent.class).getMap()));
    }
}
