package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.map.MapCursor;

/** 地图游标及其类型的可选 Fluxon 别名。 */
@Requires(classes = {
        "org.bukkit.map.MapCursor",
        "org.bukkit.map.MapCursor$Type"
})
@SuppressWarnings("deprecation")
final class NovaMapCursor {

    private NovaMapCursor() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(MapCursor.class, "x", function -> function
                .returns(Integer.class)
                .invoke(arguments -> (int) cursor(arguments).getX()));
        builder.extension(MapCursor.class, "y", function -> function
                .returns(Integer.class)
                .invoke(arguments -> (int) cursor(arguments).getY()));
        builder.extension(MapCursor.class, "direction", function -> function
                .returns(Integer.class)
                .invoke(arguments -> (int) cursor(arguments).getDirection()));
        builder.extension(MapCursor.class, "type", function -> function
                .returns(MapCursor.Type.class)
                .invoke(arguments -> cursor(arguments).getType()));
        builder.extension(MapCursor.class, "rawType", function -> function
                .returns(Integer.class)
                .invoke(arguments -> (int) cursor(arguments).getRawType()));
        builder.extension(MapCursor.class, "isVisible", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> cursor(arguments).isVisible()));
        builder.extension(MapCursor.class, "setX", function -> function
                .param("x", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    cursor(arguments).setX(argument(arguments, 1, Integer.class).byteValue());
                    return null;
                }));
        builder.extension(MapCursor.class, "setY", function -> function
                .param("y", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    cursor(arguments).setY(argument(arguments, 1, Integer.class).byteValue());
                    return null;
                }));
        builder.extension(MapCursor.class, "setDirection", function -> function
                .param("direction", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    cursor(arguments).setDirection(argument(arguments, 1, Integer.class).byteValue());
                    return null;
                }));
        builder.extension(MapCursor.class, "setRawType", function -> function
                .param("type", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    cursor(arguments).setRawType(argument(arguments, 1, Integer.class).byteValue());
                    return null;
                }));
        builder.extension(MapCursor.class, "setType", function -> function
                .param("type", MapCursor.Type.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    cursor(arguments).setType(argument(arguments, 1, MapCursor.Type.class));
                    return null;
                }));
        builder.extension(MapCursor.class, "setVisible", function -> function
                .param("visible", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    cursor(arguments).setVisible(argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(MapCursor.Type.class, "value", function -> function
                .returns(Integer.class)
                .invoke(arguments -> (int) type(arguments).getValue()));
        builder.extension(MapCursor.Type.class, "byValue", function -> function
                .param("value", Integer.class)
                .returns(MapCursor.Type.class)
                .invoke(arguments -> MapCursor.Type.byValue(
                        argument(arguments, 1, Integer.class).byteValue())));
    }

    private static MapCursor cursor(Object[] arguments) {
        return argument(arguments, 0, MapCursor.class);
    }

    private static MapCursor.Type type(Object[] arguments) {
        return argument(arguments, 0, MapCursor.Type.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
