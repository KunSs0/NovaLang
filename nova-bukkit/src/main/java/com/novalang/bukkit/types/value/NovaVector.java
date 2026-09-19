package com.novalang.bukkit.types.value;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.util.BlockVector;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

/** Spigot 1.12.2 Vector、BlockVector、EulerAngle 扩展。 */
public final class NovaVector {

    private NovaVector() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Vector.class, "clone", f -> f.returns(Vector.class).invoke(a -> vector(a).clone()));
        builder.extension(Vector.class, "blockX", f -> f.returns(Integer.class).invoke(a -> vector(a).getBlockX()));
        builder.extension(Vector.class, "blockY", f -> f.returns(Integer.class).invoke(a -> vector(a).getBlockY()));
        builder.extension(Vector.class, "blockZ", f -> f.returns(Integer.class).invoke(a -> vector(a).getBlockZ()));
        builder.extension(Vector.class, "length", f -> f.returns(Double.class).invoke(a -> vector(a).length()));
        builder.extension(Vector.class, "lengthSquared", f -> f.returns(Double.class).invoke(a -> vector(a).lengthSquared()));
        builder.extension(Vector.class, "normalize", f -> f.returns(Vector.class).invoke(a -> vector(a).normalize()));
        builder.extension(Vector.class, "zero", f -> f.returns(Vector.class).invoke(a -> vector(a).zero()));
        builder.extension(Vector.class, "x", f -> f.returns(Double.class).invoke(a -> vector(a).getX()));
        builder.extension(Vector.class, "setX", f -> f.param("x", Double.class).returns(Vector.class).invoke(a -> vector(a).setX(arg(a, 1, Double.class))));
        builder.extension(Vector.class, "y", f -> f.returns(Double.class).invoke(a -> vector(a).getY()));
        builder.extension(Vector.class, "setY", f -> f.param("y", Double.class).returns(Vector.class).invoke(a -> vector(a).setY(arg(a, 1, Double.class))));
        builder.extension(Vector.class, "z", f -> f.returns(Double.class).invoke(a -> vector(a).getZ()));
        builder.extension(Vector.class, "setZ", f -> f.param("z", Double.class).returns(Vector.class).invoke(a -> vector(a).setZ(arg(a, 1, Double.class))));
        builder.extension(Vector.class, "add", f -> f.param("vector", Vector.class).returns(Vector.class).invoke(a -> vector(a).add(arg(a, 1, Vector.class))));
        builder.extension(Vector.class, "subtract", f -> f.param("vector", Vector.class).returns(Vector.class).invoke(a -> vector(a).subtract(arg(a, 1, Vector.class))));
        builder.extension(Vector.class, "multiply", f -> f.param("factor", Double.class).returns(Vector.class).invoke(a -> vector(a).multiply(arg(a, 1, Double.class))));
        builder.extension(Vector.class, "multiply", f -> f.param("vector", Vector.class).returns(Vector.class).invoke(a -> vector(a).multiply(arg(a, 1, Vector.class))));
        builder.extension(Vector.class, "divide", f -> f.param("vector", Vector.class).returns(Vector.class).invoke(a -> vector(a).divide(arg(a, 1, Vector.class))));
        builder.extension(Vector.class, "add", f -> f.param("factor", Double.class).returns(Vector.class).invoke(a -> scalarAdd(vector(a), arg(a, 1, Double.class))));
        builder.extension(Vector.class, "subtract", f -> f.param("factor", Double.class).returns(Vector.class).invoke(a -> scalarSubtract(vector(a), arg(a, 1, Double.class))));
        builder.extension(Vector.class, "divide", f -> f.param("factor", Double.class).returns(Vector.class).invoke(a -> vector(a).multiply(1.0 / arg(a, 1, Double.class))));
        builder.extension(Vector.class, "distance", f -> f.param("vector", Vector.class).returns(Double.class).invoke(a -> vector(a).distance(arg(a, 1, Vector.class))));
        builder.extension(Vector.class, "distanceSquared", f -> f.param("vector", Vector.class).returns(Double.class).invoke(a -> vector(a).distanceSquared(arg(a, 1, Vector.class))));
        builder.extension(Vector.class, "dot", f -> f.param("vector", Vector.class).returns(Double.class).invoke(a -> vector(a).dot(arg(a, 1, Vector.class))));
        builder.extension(Vector.class, "cross", f -> f.param("vector", Vector.class).returns(Vector.class).invoke(a -> vector(a).crossProduct(arg(a, 1, Vector.class))));
        builder.extension(Vector.class, "toLocation", f -> f.param("world", org.bukkit.World.class).returns(org.bukkit.Location.class).invoke(a -> vector(a).toLocation(arg(a, 1, org.bukkit.World.class))));
        builder.extension(BlockVector.class, "clone", f -> f.returns(BlockVector.class).invoke(a -> blockVector(a).clone()));
        builder.extension(BlockVector.class, "deserialize", f -> f.param("map", java.util.Map.class).returns(BlockVector.class).invoke(NovaVector::deserializeBlockVector));
        builder.extension(EulerAngle.class, "x", f -> f.returns(Double.class).invoke(a -> angle(a).getX()));
        builder.extension(EulerAngle.class, "y", f -> f.returns(Double.class).invoke(a -> angle(a).getY()));
        builder.extension(EulerAngle.class, "z", f -> f.returns(Double.class).invoke(a -> angle(a).getZ()));
        builder.extension(EulerAngle.class, "setX", f -> f.param("x", Double.class).returns(EulerAngle.class).invoke(a -> angle(a).setX(arg(a, 1, Double.class))));
        builder.extension(EulerAngle.class, "setY", f -> f.param("y", Double.class).returns(EulerAngle.class).invoke(a -> angle(a).setY(arg(a, 1, Double.class))));
        builder.extension(EulerAngle.class, "setZ", f -> f.param("z", Double.class).returns(EulerAngle.class).invoke(a -> angle(a).setZ(arg(a, 1, Double.class))));
        builder.extension(EulerAngle.class, "add", f -> f.param("x", Double.class).param("y", Double.class).param("z", Double.class).returns(EulerAngle.class)
                .invoke(a -> angle(a).add(arg(a, 1, Double.class), arg(a, 2, Double.class), arg(a, 3, Double.class))));
        builder.extension(EulerAngle.class, "subtract", f -> f.param("x", Double.class).param("y", Double.class).param("z", Double.class).returns(EulerAngle.class)
                .invoke(a -> angle(a).subtract(arg(a, 1, Double.class), arg(a, 2, Double.class), arg(a, 3, Double.class))));
    }

    private static Vector vector(Object[] a) { return NovaTypeSupport.argument(a, 0, Vector.class); }
    private static BlockVector blockVector(Object[] a) { return NovaTypeSupport.argument(a, 0, BlockVector.class); }
    private static EulerAngle angle(Object[] a) { return NovaTypeSupport.argument(a, 0, EulerAngle.class); }
    private static <T> T arg(Object[] a, int index, Class<T> type) { return NovaTypeSupport.argument(a, index, type); }
    private static Vector scalarAdd(Vector value, Double factor) { return value.add(new Vector(factor, factor, factor)); }
    private static Vector scalarSubtract(Vector value, Double factor) { return value.subtract(new Vector(factor, factor, factor)); }
    private static BlockVector deserializeBlockVector(Object[] arguments) {
        java.util.Map<?, ?> source = NovaTypeSupport.argument(arguments, 1, java.util.Map.class);
        java.util.Map<String, Object> values = new java.util.HashMap<>();
        for (java.util.Map.Entry<?, ?> entry : source.entrySet()) {
            if (entry.getKey() instanceof String) {
                values.put((String) entry.getKey(), entry.getValue());
            }
        }
        return BlockVector.deserialize(values);
    }
}
