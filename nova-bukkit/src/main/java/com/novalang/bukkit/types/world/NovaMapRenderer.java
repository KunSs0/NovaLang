package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/** Spigot 1.12.2 地图渲染器的 Fluxon 函数别名。 */
public final class NovaMapRenderer {

    private NovaMapRenderer() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(MapRenderer.class, "isContextual", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, MapRenderer.class).isContextual()));
        builder.extension(MapRenderer.class, "initialize", function -> function
                .param("map", MapView.class)
                .invoke(arguments -> {
                    renderer(arguments).initialize(argument(arguments, 1, MapView.class));
                    return null;
                }));
        builder.extension(MapRenderer.class, "render", function -> function
                .param("map", MapView.class)
                .param("canvas", MapCanvas.class)
                .param("player", Player.class)
                .invoke(arguments -> {
                    renderer(arguments).render(
                            argument(arguments, 1, MapView.class),
                            argument(arguments, 2, MapCanvas.class),
                            argument(arguments, 3, Player.class));
                    return null;
                }));
    }

    private static MapRenderer renderer(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, MapRenderer.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
