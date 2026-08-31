package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapView;

import java.awt.Image;

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
        builder.extension(MapCanvas.class, "setCursors", function -> function
                .param("cursors", MapCursorCollection.class)
                .invoke(arguments -> {
                    canvas(arguments).setCursors(argument(arguments, 1, MapCursorCollection.class));
                    return null;
                }));
        builder.extension(MapCanvas.class, "setPixel", function -> function
                .param("x", Integer.class)
                .param("y", Integer.class)
                .param("color", Integer.class)
                .invoke(arguments -> {
                    canvas(arguments).setPixel(
                            argument(arguments, 1, Integer.class),
                            argument(arguments, 2, Integer.class),
                            argument(arguments, 3, Integer.class).byteValue());
                    return null;
                }));
        builder.extension(MapCanvas.class, "getPixel", function -> function
                .param("x", Integer.class)
                .param("y", Integer.class)
                .returns(Integer.class)
                .invoke(arguments -> (int) canvas(arguments).getPixel(
                        argument(arguments, 1, Integer.class),
                        argument(arguments, 2, Integer.class))));
        builder.extension(MapCanvas.class, "getBasePixel", function -> function
                .param("x", Integer.class)
                .param("y", Integer.class)
                .returns(Integer.class)
                .invoke(arguments -> (int) canvas(arguments).getBasePixel(
                        argument(arguments, 1, Integer.class),
                        argument(arguments, 2, Integer.class))));
        builder.extension(MapCanvas.class, "drawImage", function -> function
                .param("x", Integer.class)
                .param("y", Integer.class)
                .param("image", Image.class)
                .invoke(arguments -> {
                    canvas(arguments).drawImage(
                            argument(arguments, 1, Integer.class),
                            argument(arguments, 2, Integer.class),
                            argument(arguments, 3, Image.class));
                    return null;
                }));
        builder.extension(MapCanvas.class, "drawText", function -> function
                .param("x", Integer.class)
                .param("y", Integer.class)
                .param("font", MapFont.class)
                .param("text", String.class)
                .invoke(arguments -> {
                    canvas(arguments).drawText(
                            argument(arguments, 1, Integer.class),
                            argument(arguments, 2, Integer.class),
                            argument(arguments, 3, MapFont.class),
                            argument(arguments, 4, String.class));
                    return null;
                }));
    }

    private static MapCanvas canvas(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, MapCanvas.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
