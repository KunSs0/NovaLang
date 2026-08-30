package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/** Bukkit Location 构造函数和 Fluxon 函数别名。 */
final class BukkitLocationJavaTypes {

    private BukkitLocationJavaTypes() {
    }

    static void register(JavaTypes.Builder builder) {
        registerGlobals(builder);
        registerExtensions(builder);
    }

    private static void registerGlobals(JavaTypes.Builder builder) {
        builder.globalFunction("location", function -> function
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .returns(Location.class)
                .invoke3(Double.class, Double.class, Double.class,
                        (x, y, z) -> new Location(null, x, y, z)));
        builder.globalFunction("location", function -> function
                .param("world", World.class)
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .returns(Location.class)
                .invoke(arguments -> new Location(
                        BukkitJavaTypeSupport.argument(arguments, 0, World.class),
                        BukkitJavaTypeSupport.argument(arguments, 1, Double.class),
                        BukkitJavaTypeSupport.argument(arguments, 2, Double.class),
                        BukkitJavaTypeSupport.argument(arguments, 3, Double.class))));
        builder.globalFunction("location", function -> function
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .param("yaw", Float.class)
                .param("pitch", Float.class)
                .returns(Location.class)
                .invoke(arguments -> new Location(
                        null,
                        BukkitJavaTypeSupport.argument(arguments, 0, Double.class),
                        BukkitJavaTypeSupport.argument(arguments, 1, Double.class),
                        BukkitJavaTypeSupport.argument(arguments, 2, Double.class),
                        BukkitJavaTypeSupport.argument(arguments, 3, Float.class),
                        BukkitJavaTypeSupport.argument(arguments, 4, Float.class))));
        builder.globalFunction("location", function -> function
                .param("world", World.class)
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .param("yaw", Float.class)
                .param("pitch", Float.class)
                .returns(Location.class)
                .invoke(arguments -> new Location(
                        BukkitJavaTypeSupport.argument(arguments, 0, World.class),
                        BukkitJavaTypeSupport.argument(arguments, 1, Double.class),
                        BukkitJavaTypeSupport.argument(arguments, 2, Double.class),
                        BukkitJavaTypeSupport.argument(arguments, 3, Double.class),
                        BukkitJavaTypeSupport.argument(arguments, 4, Float.class),
                        BukkitJavaTypeSupport.argument(arguments, 5, Float.class))));
    }

    private static void registerExtensions(JavaTypes.Builder builder) {
        builder.extension(Location.class, "x", function -> function
                .returns(Double.class)
                .invoke(arguments -> BukkitJavaTypeSupport.argument(arguments, 0, Location.class).getX()));
        builder.extension(Location.class, "setX", function -> function
                .param("value", Double.class)
                .returns(Location.class)
                .invoke(BukkitLocationJavaTypes::setX));
        builder.extension(Location.class, "y", function -> function
                .returns(Double.class)
                .invoke(arguments -> BukkitJavaTypeSupport.argument(arguments, 0, Location.class).getY()));
        builder.extension(Location.class, "setY", function -> function
                .param("value", Double.class)
                .returns(Location.class)
                .invoke(BukkitLocationJavaTypes::setY));
        builder.extension(Location.class, "z", function -> function
                .returns(Double.class)
                .invoke(arguments -> BukkitJavaTypeSupport.argument(arguments, 0, Location.class).getZ()));
        builder.extension(Location.class, "setZ", function -> function
                .param("value", Double.class)
                .returns(Location.class)
                .invoke(BukkitLocationJavaTypes::setZ));
        builder.extension(Location.class, "yaw", function -> function
                .returns(Float.class)
                .invoke(arguments -> BukkitJavaTypeSupport.argument(arguments, 0, Location.class).getYaw()));
        builder.extension(Location.class, "setYaw", function -> function
                .param("value", Float.class)
                .returns(Location.class)
                .invoke(BukkitLocationJavaTypes::setYaw));
        builder.extension(Location.class, "pitch", function -> function
                .returns(Float.class)
                .invoke(arguments -> BukkitJavaTypeSupport.argument(arguments, 0, Location.class).getPitch()));
        builder.extension(Location.class, "setPitch", function -> function
                .param("value", Float.class)
                .returns(Location.class)
                .invoke(BukkitLocationJavaTypes::setPitch));
        builder.extension(Location.class, "direction", function -> function
                .returns(Vector.class)
                .invoke(arguments -> BukkitJavaTypeSupport.argument(arguments, 0, Location.class).getDirection()));
        builder.extension(Location.class, "setDirection", function -> function
                .param("direction", Vector.class)
                .returns(Location.class)
                .invoke(arguments -> BukkitJavaTypeSupport.argument(arguments, 0, Location.class)
                        .setDirection(BukkitJavaTypeSupport.argument(arguments, 1, Vector.class))));
        builder.extension(Location.class, "world", function -> function
                .returns(JavaTypeRef.javaType(World.class).nullable())
                .invoke(arguments -> BukkitJavaTypeSupport.argument(arguments, 0, Location.class).getWorld()));
        builder.extension(Location.class, "setWorld", function -> function
                .param("value", Object.class)
                .returns(Location.class)
                .invoke(BukkitLocationJavaTypes::setWorld));
    }

    private static Location setX(Object[] arguments) {
        Location location = BukkitJavaTypeSupport.argument(arguments, 0, Location.class);
        location.setX(BukkitJavaTypeSupport.argument(arguments, 1, Double.class));
        return location;
    }

    private static Location setY(Object[] arguments) {
        Location location = BukkitJavaTypeSupport.argument(arguments, 0, Location.class);
        location.setY(BukkitJavaTypeSupport.argument(arguments, 1, Double.class));
        return location;
    }

    private static Location setZ(Object[] arguments) {
        Location location = BukkitJavaTypeSupport.argument(arguments, 0, Location.class);
        location.setZ(BukkitJavaTypeSupport.argument(arguments, 1, Double.class));
        return location;
    }

    private static Location setYaw(Object[] arguments) {
        Location location = BukkitJavaTypeSupport.argument(arguments, 0, Location.class);
        location.setYaw(BukkitJavaTypeSupport.argument(arguments, 1, Float.class));
        return location;
    }

    private static Location setPitch(Object[] arguments) {
        Location location = BukkitJavaTypeSupport.argument(arguments, 0, Location.class);
        location.setPitch(BukkitJavaTypeSupport.argument(arguments, 1, Float.class));
        return location;
    }

    private static Location setWorld(Object[] arguments) {
        Location location = BukkitJavaTypeSupport.argument(arguments, 0, Location.class);
        Object value = arguments.length > 1 ? arguments[1] : null;
        World world;
        if (value instanceof World) {
            world = (World) value;
        } else if (value instanceof String) {
            world = Bukkit.getWorld((String) value);
        } else if (value instanceof Location) {
            world = ((Location) value).getWorld();
        } else {
            throw new IllegalArgumentException("参数必须是 World、String 或 Location");
        }
        location.setWorld(world);
        return location;
    }
}
