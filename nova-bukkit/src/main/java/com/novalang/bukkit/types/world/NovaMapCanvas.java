package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapView;

/** 地图画布的可选 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.map.MapCanvas"})
final class NovaMapCanvas {

    private NovaMapCanvas() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(MapCanvas.class, "mapView", function -> function
                .returns(MapView.class)
                .invoke(arguments -> canvas(arguments).getMapView()));
        builder.extension(MapCanvas.class, "cursors", function -> function
                .returns(MapCursorCollection.class)
                .invoke(arguments -> canvas(arguments).getCursors()));
    }

    private static MapCanvas canvas(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, MapCanvas.class);
    }
}
