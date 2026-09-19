package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;
import org.bukkit.inventory.meta.MapMeta;

/** 地图物品元数据的可选编译期别名。 */
@Requires(classes = {"org.bukkit.inventory.meta.MapMeta"})
public final class NovaMapMeta {

    private NovaMapMeta() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableString = JavaTypeRef.javaType(String.class).nullable();
        JavaTypeRef nullableColor = JavaTypeRef.javaType(Color.class).nullable();
        builder.extension(MapMeta.class, "isScaling", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).isScaling()));
        builder.extension(MapMeta.class, "setScaling", function -> function
                .param("scaling", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setScaling(argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(MapMeta.class, "hasLocationName", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasLocationName()));
        builder.extension(MapMeta.class, "locationName", function -> function
                .returns(nullableString)
                .invoke(arguments -> meta(arguments).getLocationName()));
        builder.extension(MapMeta.class, "setLocationName", function -> function
                .param("name", nullableString)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setLocationName(argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(MapMeta.class, "hasColor", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasColor()));
        builder.extension(MapMeta.class, "color", function -> function
                .returns(nullableColor)
                .invoke(arguments -> meta(arguments).getColor()));
        builder.extension(MapMeta.class, "setColor", function -> function
                .param("color", nullableColor)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setColor(argument(arguments, 1, Color.class));
                    return null;
                }));
        builder.extension(MapMeta.class, "clone", function -> function
                .returns(MapMeta.class)
                .invoke(arguments -> meta(arguments).clone()));
    }

    private static MapMeta meta(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, MapMeta.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
