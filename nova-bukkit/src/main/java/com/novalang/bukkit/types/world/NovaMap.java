package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.World;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/** Spigot 1.12.2 MapView 与 MapView.Scale 扩展。 */
final class NovaMap {

    private NovaMap() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableWorld = JavaTypeRef.javaType(World.class).nullable();
        builder.extension(MapView.class, "id", f -> f.returns(Integer.class).invoke(a -> map(a).getId()));
        builder.extension(MapView.class, "isVirtual", f -> f.returns(Boolean.class).invoke(a -> map(a).isVirtual()));
        builder.extension(MapView.class, "scale", f -> f.returns(MapView.Scale.class).invoke(a -> map(a).getScale()));
        builder.extension(MapView.class, "setScale", f -> f.param("scale", MapView.Scale.class).invoke(a -> { map(a).setScale(arg(a, 1, MapView.Scale.class)); return null; }));
        builder.extension(MapView.class, "centerX", f -> f.returns(Integer.class).invoke(a -> map(a).getCenterX()));
        builder.extension(MapView.class, "centerZ", f -> f.returns(Integer.class).invoke(a -> map(a).getCenterZ()));
        builder.extension(MapView.class, "setCenterX", f -> f.param("x", Integer.class).invoke(a -> { map(a).setCenterX(arg(a, 1, Integer.class)); return null; }));
        builder.extension(MapView.class, "setCenterZ", f -> f.param("z", Integer.class).invoke(a -> { map(a).setCenterZ(arg(a, 1, Integer.class)); return null; }));
        builder.extension(MapView.class, "world", f -> f.returns(nullableWorld).invoke(a -> map(a).getWorld()));
        builder.extension(MapView.class, "setWorld", f -> f.param("world", World.class).invoke(a -> { map(a).setWorld(arg(a, 1, World.class)); return null; }));
        builder.extension(MapView.class, "renderers", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(MapRenderer.class))).invoke(a -> map(a).getRenderers()));
        builder.extension(MapView.class, "addRenderer", f -> f.param("renderer", MapRenderer.class).invoke(a -> { map(a).addRenderer(arg(a, 1, MapRenderer.class)); return null; }));
        builder.extension(MapView.class, "removeRenderer", f -> f.param("renderer", MapRenderer.class).invoke(a -> { map(a).removeRenderer(arg(a, 1, MapRenderer.class)); return null; }));
        builder.extension(MapView.Scale.class, "value", f -> f.returns(Integer.class).invoke(a -> (int) scale(a).getValue()));
        builder.extension(MapView.Scale.class, "valueOf", f -> f.param("value", Integer.class).returns(MapView.Scale.class).invoke(a -> MapView.Scale.valueOf(arg(a, 1, Integer.class).byteValue())));
    }

    private static MapView map(Object[] a) { return NovaTypeSupport.argument(a, 0, MapView.class); }
    private static MapView.Scale scale(Object[] a) { return NovaTypeSupport.argument(a, 0, MapView.Scale.class); }
    private static <T> T arg(Object[] a, int index, Class<T> type) { return NovaTypeSupport.argument(a, index, type); }
}
