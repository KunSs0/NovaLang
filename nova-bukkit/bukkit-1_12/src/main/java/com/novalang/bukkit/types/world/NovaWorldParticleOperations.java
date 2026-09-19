package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

/** Spigot 1.12.2 World 的无附加数据粒子操作别名。 */
final class NovaWorldParticleOperations {

    private NovaWorldParticleOperations() {
    }

    static void register(JavaTypes.Builder builder) {
        registerAtLocation(builder, Particle.class);
        registerAtLocation(builder, String.class);
        registerAtCoordinates(builder, Particle.class);
        registerAtCoordinates(builder, String.class);
    }

    private static void registerAtLocation(JavaTypes.Builder builder, Class<?> particleType) {
        builder.extension(World.class, "spawnParticle", function -> function
                .param("particle", particleType)
                .param("location", Location.class)
                .param("count", Integer.class)
                .invoke(arguments -> {
                    world(arguments).spawnParticle(
                            particle(arguments, particleType),
                            argument(arguments, 2, Location.class),
                            argument(arguments, 3, Integer.class));
                    return null;
                }));
        builder.extension(World.class, "spawnParticle", function -> function
                .param("particle", particleType)
                .param("location", Location.class)
                .param("count", Integer.class)
                .param("offsetX", Double.class)
                .param("offsetY", Double.class)
                .param("offsetZ", Double.class)
                .invoke(arguments -> {
                    world(arguments).spawnParticle(
                            particle(arguments, particleType),
                            argument(arguments, 2, Location.class),
                            argument(arguments, 3, Integer.class),
                            argument(arguments, 4, Double.class),
                            argument(arguments, 5, Double.class),
                            argument(arguments, 6, Double.class));
                    return null;
                }));
        builder.extension(World.class, "spawnParticle", function -> function
                .param("particle", particleType)
                .param("location", Location.class)
                .param("count", Integer.class)
                .param("offsetX", Double.class)
                .param("offsetY", Double.class)
                .param("offsetZ", Double.class)
                .param("extra", Double.class)
                .invoke(arguments -> {
                    spawnParticle(
                            world(arguments),
                            particle(arguments, particleType),
                            argument(arguments, 2, Location.class),
                            argument(arguments, 3, Integer.class),
                            argument(arguments, 4, Double.class),
                            argument(arguments, 5, Double.class),
                            argument(arguments, 6, Double.class),
                            argument(arguments, 7, Double.class).doubleValue());
                    return null;
                }));
    }

    private static void registerAtCoordinates(JavaTypes.Builder builder, Class<?> particleType) {
        builder.extension(World.class, "spawnParticle", function -> function
                .param("particle", particleType)
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .param("count", Integer.class)
                .invoke(arguments -> {
                    world(arguments).spawnParticle(
                            particle(arguments, particleType),
                            argument(arguments, 2, Double.class),
                            argument(arguments, 3, Double.class),
                            argument(arguments, 4, Double.class),
                            argument(arguments, 5, Integer.class));
                    return null;
                }));
        builder.extension(World.class, "spawnParticle", function -> function
                .param("particle", particleType)
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .param("count", Integer.class)
                .param("offsetX", Double.class)
                .param("offsetY", Double.class)
                .param("offsetZ", Double.class)
                .invoke(arguments -> {
                    world(arguments).spawnParticle(
                            particle(arguments, particleType),
                            argument(arguments, 2, Double.class),
                            argument(arguments, 3, Double.class),
                            argument(arguments, 4, Double.class),
                            argument(arguments, 5, Integer.class),
                            argument(arguments, 6, Double.class),
                            argument(arguments, 7, Double.class),
                            argument(arguments, 8, Double.class));
                    return null;
                }));
        builder.extension(World.class, "spawnParticle", function -> function
                .param("particle", particleType)
                .param("x", Double.class)
                .param("y", Double.class)
                .param("z", Double.class)
                .param("count", Integer.class)
                .param("offsetX", Double.class)
                .param("offsetY", Double.class)
                .param("offsetZ", Double.class)
                .param("extra", Double.class)
                .invoke(arguments -> {
                    spawnParticle(
                            world(arguments),
                            particle(arguments, particleType),
                            argument(arguments, 2, Double.class),
                            argument(arguments, 3, Double.class),
                            argument(arguments, 4, Double.class),
                            argument(arguments, 5, Integer.class),
                            argument(arguments, 6, Double.class),
                            argument(arguments, 7, Double.class),
                            argument(arguments, 8, Double.class),
                            argument(arguments, 9, Double.class).doubleValue());
                    return null;
                }));
    }

    private static Particle particle(Object[] arguments, Class<?> particleType) {
        if (particleType == String.class) {
            return NovaTypeSupport.findEnum(Particle.class, argument(arguments, 1, String.class));
        }
        return argument(arguments, 1, Particle.class);
    }

    private static void spawnParticle(World world,
                                      Particle particle,
                                      Location location,
                                      int count,
                                      double offsetX,
                                      double offsetY,
                                      double offsetZ,
                                      double extra) {
        world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
    }

    private static void spawnParticle(World world,
                                      Particle particle,
                                      double x,
                                      double y,
                                      double z,
                                      int count,
                                      double offsetX,
                                      double offsetY,
                                      double offsetZ,
                                      double extra) {
        world.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra);
    }

    private static World world(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, World.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
