package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursorCollection;

/** Spigot 1.12.2 地图游标集合的 Fluxon 函数别名。 */
public final class NovaMapCursorCollection {

    private NovaMapCursorCollection() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(MapCursorCollection.class, "size", function -> function.returns(Integer.class).invoke(arguments -> collection(arguments).size()));
        builder.extension(MapCursorCollection.class, "getCursor", function -> function.param("index", Integer.class).returns(MapCursor.class)
                .invoke(arguments -> collection(arguments).getCursor(argument(arguments, 1, Integer.class))));
        builder.extension(MapCursorCollection.class, "removeCursor", function -> function.param("cursor", MapCursor.class).returns(Void.TYPE).invoke(arguments -> {
            collection(arguments).removeCursor(argument(arguments, 1, MapCursor.class));
            return null;
        }));
        builder.extension(MapCursorCollection.class, "addCursor", function -> function.param("cursor", MapCursor.class).returns(MapCursor.class)
                .invoke(arguments -> collection(arguments).addCursor(argument(arguments, 1, MapCursor.class))));
        registerCoordinates(builder, 0);
        registerCoordinates(builder, 1);
        registerCoordinates(builder, 2);
    }

    private static void registerCoordinates(JavaTypes.Builder builder, int mode) {
        if (mode == 0) {
            builder.extension(MapCursorCollection.class, "addCursor", function -> function.param("x", Integer.class).param("y", Integer.class).param("direction", Integer.class).returns(MapCursor.class)
                    .invoke(arguments -> collection(arguments).addCursor(byteArgument(arguments, 1), byteArgument(arguments, 2), byteArgument(arguments, 3))));
            return;
        }
        if (mode == 1) {
            builder.extension(MapCursorCollection.class, "addCursor", function -> function.param("x", Integer.class).param("y", Integer.class).param("direction", Integer.class).param("type", Integer.class).returns(MapCursor.class)
                    .invoke(arguments -> collection(arguments).addCursor(byteArgument(arguments, 1), byteArgument(arguments, 2), byteArgument(arguments, 3), byteArgument(arguments, 4))));
            return;
        }
        builder.extension(MapCursorCollection.class, "addCursor", function -> function.param("x", Integer.class).param("y", Integer.class).param("direction", Integer.class).param("type", Integer.class).param("visible", Boolean.class).returns(MapCursor.class)
                .invoke(arguments -> collection(arguments).addCursor(byteArgument(arguments, 1), byteArgument(arguments, 2), byteArgument(arguments, 3), byteArgument(arguments, 4), argument(arguments, 5, Boolean.class))));
    }

    private static MapCursorCollection collection(Object[] arguments) {
        return argument(arguments, 0, MapCursorCollection.class);
    }

    private static byte byteArgument(Object[] arguments, int index) {
        return argument(arguments, index, Integer.class).byteValue();
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
