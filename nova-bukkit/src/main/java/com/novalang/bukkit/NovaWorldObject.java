package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

/** Spigot 1.12.2 的 WorldBorder、WorldCreator 扩展。 */
final class NovaWorldObject {

    private NovaWorldObject() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(WorldBorder.class, "reset", f -> f.invoke(a -> { support(a).reset(); return null; }));
        builder.extension(WorldBorder.class, "size", f -> f.returns(Double.class).invoke(a -> support(a).getSize()));
        builder.extension(WorldBorder.class, "setSize", f -> f.param("size", Double.class).invoke(a -> { support(a).setSize(arg(a, 1, Double.class)); return null; }));
        builder.extension(WorldBorder.class, "setSize", f -> f.param("size", Double.class).param("seconds", Long.class).invoke(a -> { support(a).setSize(arg(a, 1, Double.class), arg(a, 2, Long.class)); return null; }));
        builder.extension(WorldBorder.class, "center", f -> f.returns(org.bukkit.Location.class).invoke(a -> support(a).getCenter()));
        builder.extension(WorldBorder.class, "setCenter", f -> f.param("x", Double.class).param("z", Double.class).invoke(a -> { support(a).setCenter(arg(a, 1, Double.class), arg(a, 2, Double.class)); return null; }));
        builder.extension(WorldBorder.class, "damageBuffer", f -> f.returns(Double.class).invoke(a -> support(a).getDamageBuffer()));
        builder.extension(WorldBorder.class, "setDamageBuffer", f -> f.param("blocks", Double.class).invoke(a -> { support(a).setDamageBuffer(arg(a, 1, Double.class)); return null; }));
        builder.extension(WorldBorder.class, "damageAmount", f -> f.returns(Double.class).invoke(a -> support(a).getDamageAmount()));
        builder.extension(WorldBorder.class, "setDamageAmount", f -> f.param("damage", Double.class).invoke(a -> { support(a).setDamageAmount(arg(a, 1, Double.class)); return null; }));
        builder.extension(WorldBorder.class, "warningTime", f -> f.returns(Integer.class).invoke(a -> support(a).getWarningTime()));
        builder.extension(WorldBorder.class, "setWarningTime", f -> f.param("seconds", Integer.class).invoke(a -> { support(a).setWarningTime(arg(a, 1, Integer.class)); return null; }));
        builder.extension(WorldBorder.class, "warningDistance", f -> f.returns(Integer.class).invoke(a -> support(a).getWarningDistance()));
        builder.extension(WorldBorder.class, "setWarningDistance", f -> f.param("blocks", Integer.class).invoke(a -> { support(a).setWarningDistance(arg(a, 1, Integer.class)); return null; }));
        builder.extension(WorldBorder.class, "isInside", f -> f.param("location", org.bukkit.Location.class).returns(Boolean.class).invoke(a -> support(a).isInside(arg(a, 1, org.bukkit.Location.class))));

        builder.extension(WorldCreator.class, "copy", f -> f.param("world", World.class).returns(WorldCreator.class).invoke(a -> supportCreator(a).copy(arg(a, 1, World.class))));
        builder.extension(WorldCreator.class, "copy", f -> f.param("creator", WorldCreator.class).returns(WorldCreator.class).invoke(a -> supportCreator(a).copy(arg(a, 1, WorldCreator.class))));
        builder.extension(WorldCreator.class, "name", f -> f.returns(String.class).invoke(a -> supportCreator(a).name()));
        builder.extension(WorldCreator.class, "seed", f -> f.returns(Long.class).invoke(a -> supportCreator(a).seed()));
        builder.extension(WorldCreator.class, "seed", f -> f.param("seed", Long.class).returns(WorldCreator.class).invoke(a -> supportCreator(a).seed(arg(a, 1, Long.class))));
        builder.extension(WorldCreator.class, "environment", f -> f.returns(World.Environment.class).invoke(a -> supportCreator(a).environment()));
        builder.extension(WorldCreator.class, "environment", f -> f.param("environment", World.Environment.class).returns(WorldCreator.class).invoke(a -> supportCreator(a).environment(arg(a, 1, World.Environment.class))));
        builder.extension(WorldCreator.class, "type", f -> f.returns(WorldType.class).invoke(a -> supportCreator(a).type()));
        builder.extension(WorldCreator.class, "type", f -> f.param("type", WorldType.class).returns(WorldCreator.class).invoke(a -> supportCreator(a).type(arg(a, 1, WorldType.class))));
        builder.extension(WorldCreator.class, "generator", f -> f.returns(JavaTypeRef.javaType(org.bukkit.generator.ChunkGenerator.class).nullable()).invoke(a -> supportCreator(a).generator()));
        builder.extension(WorldCreator.class, "generator", f -> f.param("name", String.class).returns(WorldCreator.class).invoke(a -> supportCreator(a).generator(arg(a, 1, String.class))));
        builder.extension(WorldCreator.class, "generateStructures", f -> f.returns(Boolean.class).invoke(a -> supportCreator(a).generateStructures()));
        builder.extension(WorldCreator.class, "generateStructures", f -> f.param("generate", Boolean.class).returns(WorldCreator.class).invoke(a -> supportCreator(a).generateStructures(arg(a, 1, Boolean.class))));
        builder.extension(WorldCreator.class, "createWorld", f -> f.returns(JavaTypeRef.javaType(World.class).nullable()).invoke(a -> supportCreator(a).createWorld()));
    }

    private static WorldBorder support(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, WorldBorder.class);
    }

    private static WorldCreator supportCreator(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, WorldCreator.class);
    }

    private static <T> T arg(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
