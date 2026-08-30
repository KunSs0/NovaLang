package com.novalang.bukkit.types.value;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/** Bukkit Location 构造函数和 Fluxon 函数别名。 */
public final class NovaLocation {

    private NovaLocation() {
    }

    public static void register(JavaTypes.Builder builder) {
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
                        NovaTypeSupport.argument(arguments, 0, World.class),
                        NovaTypeSupport.argument(arguments, 1, Double.class),
                        NovaTypeSupport.argument(arguments, 2, Double.class),
                        NovaTypeSupport.argument(arguments, 3, Double.class))));
        builder.globalFunction("location", function -> function
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .param("yaw", Float.class)
                .param("pitch", Float.class)
                .returns(Location.class)
                .invoke(arguments -> new Location(
                        null,
                        NovaTypeSupport.argument(arguments, 0, Double.class),
                        NovaTypeSupport.argument(arguments, 1, Double.class),
                        NovaTypeSupport.argument(arguments, 2, Double.class),
                        NovaTypeSupport.argument(arguments, 3, Float.class),
                        NovaTypeSupport.argument(arguments, 4, Float.class))));
        builder.globalFunction("location", function -> function
                .param("world", World.class)
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .param("yaw", Float.class)
                .param("pitch", Float.class)
                .returns(Location.class)
                .invoke(arguments -> new Location(
                        NovaTypeSupport.argument(arguments, 0, World.class),
                        NovaTypeSupport.argument(arguments, 1, Double.class),
                        NovaTypeSupport.argument(arguments, 2, Double.class),
                        NovaTypeSupport.argument(arguments, 3, Double.class),
                        NovaTypeSupport.argument(arguments, 4, Float.class),
                        NovaTypeSupport.argument(arguments, 5, Float.class))));
    }

    private static void registerExtensions(JavaTypes.Builder builder) {
        builder.extension(Location.class, "clone", function -> function
                .returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).clone()));
        builder.extension(Location.class, "blockX", function -> function
                .returns(Integer.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).getBlockX()));
        builder.extension(Location.class, "blockY", function -> function
                .returns(Integer.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).getBlockY()));
        builder.extension(Location.class, "blockZ", function -> function
                .returns(Integer.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).getBlockZ()));
        builder.extension(Location.class, "length", function -> function
                .returns(Double.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).length()));
        builder.extension(Location.class, "lengthSquared", function -> function
                .returns(Double.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).lengthSquared()));
        builder.extension(Location.class, "toVector", function -> function
                .returns(Vector.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).toVector()));
        builder.extension(Location.class, "zero", function -> function
                .returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).zero()));
        builder.extension(Location.class, "serialize", function -> function
                .returns(java.util.Map.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).serialize()));
        builder.extension(Location.class, "x", function -> function
                .returns(Double.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).getX()));
        builder.extension(Location.class, "setX", function -> function
                .param("value", Double.class)
                .returns(Location.class)
                .invoke(NovaLocation::setX));
        builder.extension(Location.class, "y", function -> function
                .returns(Double.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).getY()));
        builder.extension(Location.class, "setY", function -> function
                .param("value", Double.class)
                .returns(Location.class)
                .invoke(NovaLocation::setY));
        builder.extension(Location.class, "z", function -> function
                .returns(Double.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).getZ()));
        builder.extension(Location.class, "setZ", function -> function
                .param("value", Double.class)
                .returns(Location.class)
                .invoke(NovaLocation::setZ));
        builder.extension(Location.class, "yaw", function -> function
                .returns(Float.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).getYaw()));
        builder.extension(Location.class, "setYaw", function -> function
                .param("value", Float.class)
                .returns(Location.class)
                .invoke(NovaLocation::setYaw));
        builder.extension(Location.class, "pitch", function -> function
                .returns(Float.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).getPitch()));
        builder.extension(Location.class, "setPitch", function -> function
                .param("value", Float.class)
                .returns(Location.class)
                .invoke(NovaLocation::setPitch));
        builder.extension(Location.class, "direction", function -> function
                .returns(Vector.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).getDirection()));
        builder.extension(Location.class, "setDirection", function -> function
                .param("direction", Vector.class)
                .returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class)
                        .setDirection(NovaTypeSupport.argument(arguments, 1, Vector.class))));
        builder.extension(Location.class, "world", function -> function
                .returns(JavaTypeRef.javaType(World.class).nullable())
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).getWorld()));
        builder.extension(Location.class, "setWorld", function -> function
                .param("value", Object.class)
                .returns(Location.class)
                .invoke(NovaLocation::setWorld));
        builder.extension(Location.class, "add", function -> function
                .param("location", Location.class)
                .returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class)
                        .add(NovaTypeSupport.argument(arguments, 1, Location.class))));
        builder.extension(Location.class, "add", function -> function
                .param("vector", Vector.class)
                .returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class)
                        .add(NovaTypeSupport.argument(arguments, 1, Vector.class))));
        builder.extension(Location.class, "add", function -> function
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).add(
                        NovaTypeSupport.argument(arguments, 1, Double.class),
                        NovaTypeSupport.argument(arguments, 2, Double.class),
                        NovaTypeSupport.argument(arguments, 3, Double.class))));
        builder.extension(Location.class, "subtract", function -> function
                .param("location", Location.class)
                .returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class)
                        .subtract(NovaTypeSupport.argument(arguments, 1, Location.class))));
        builder.extension(Location.class, "subtract", function -> function
                .param("vector", Vector.class)
                .returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class)
                        .subtract(NovaTypeSupport.argument(arguments, 1, Vector.class))));
        builder.extension(Location.class, "subtract", function -> function
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class).subtract(
                        NovaTypeSupport.argument(arguments, 1, Double.class),
                        NovaTypeSupport.argument(arguments, 2, Double.class),
                        NovaTypeSupport.argument(arguments, 3, Double.class))));
        builder.extension(Location.class, "multiply", function -> function
                .param("location", Location.class)
                .returns(Location.class)
                .invoke(NovaLocation::multiplyLocation));
        builder.extension(Location.class, "multiply", function -> function
                .param("factor", Double.class)
                .returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class)
                        .multiply(NovaTypeSupport.argument(arguments, 1, Double.class))));
        builder.extension(Location.class, "divide", function -> function
                .param("location", Location.class)
                .returns(Location.class)
                .invoke(NovaLocation::divideLocation));
        builder.extension(Location.class, "divide", function -> function
                .param("factor", Double.class)
                .returns(Location.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class)
                        .multiply(1.0 / NovaTypeSupport.argument(arguments, 1, Double.class))));
        builder.extension(Location.class, "distance", function -> function
                .param("location", Location.class)
                .returns(Double.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class)
                        .distance(NovaTypeSupport.argument(arguments, 1, Location.class))));
        builder.extension(Location.class, "distanceSquared", function -> function
                .param("location", Location.class)
                .returns(Double.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Location.class)
                        .distanceSquared(NovaTypeSupport.argument(arguments, 1, Location.class))));
    }

    private static Location setX(Object[] arguments) {
        Location location = NovaTypeSupport.argument(arguments, 0, Location.class);
        location.setX(NovaTypeSupport.argument(arguments, 1, Double.class));
        return location;
    }

    private static Location setY(Object[] arguments) {
        Location location = NovaTypeSupport.argument(arguments, 0, Location.class);
        location.setY(NovaTypeSupport.argument(arguments, 1, Double.class));
        return location;
    }

    private static Location setZ(Object[] arguments) {
        Location location = NovaTypeSupport.argument(arguments, 0, Location.class);
        location.setZ(NovaTypeSupport.argument(arguments, 1, Double.class));
        return location;
    }

    private static Location setYaw(Object[] arguments) {
        Location location = NovaTypeSupport.argument(arguments, 0, Location.class);
        location.setYaw(NovaTypeSupport.argument(arguments, 1, Float.class));
        return location;
    }

    private static Location setPitch(Object[] arguments) {
        Location location = NovaTypeSupport.argument(arguments, 0, Location.class);
        location.setPitch(NovaTypeSupport.argument(arguments, 1, Float.class));
        return location;
    }

    private static Location setWorld(Object[] arguments) {
        Location location = NovaTypeSupport.argument(arguments, 0, Location.class);
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

    private static Location multiplyLocation(Object[] arguments) {
        Location location = NovaTypeSupport.argument(arguments, 0, Location.class);
        Location value = NovaTypeSupport.argument(arguments, 1, Location.class);
        location.setX(location.getX() * value.getX());
        location.setY(location.getY() * value.getY());
        location.setZ(location.getZ() * value.getZ());
        return location;
    }

    private static Location divideLocation(Object[] arguments) {
        Location location = NovaTypeSupport.argument(arguments, 0, Location.class);
        Location value = NovaTypeSupport.argument(arguments, 1, Location.class);
        location.setX(location.getX() / value.getX());
        location.setY(location.getY() / value.getY());
        location.setZ(location.getZ() / value.getZ());
        return location;
    }
}
