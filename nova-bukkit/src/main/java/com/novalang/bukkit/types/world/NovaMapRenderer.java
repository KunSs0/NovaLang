package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.map.MapRenderer;

/** Spigot 1.12.2 地图渲染器的 Fluxon 函数别名。 */
public final class NovaMapRenderer {

    private NovaMapRenderer() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(MapRenderer.class, "isContextual", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, MapRenderer.class).isContextual()));
    }
}
