package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Minecart;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

/** Minecart 在 Spigot 1.12.2 可用的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.entity.Minecart"})
final class NovaMinecart {

    private NovaMinecart() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Minecart.class, "damage", function -> function.returns(Double.class)
                .invoke(arguments -> minecart(arguments).getDamage()));
        builder.extension(Minecart.class, "setDamage", function -> function.param("damage", Double.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    minecart(arguments).setDamage(NovaTypeSupport.argument(arguments, 1, Double.class));
                    return null;
                }));
        builder.extension(Minecart.class, "maxSpeed", function -> function.returns(Double.class)
                .invoke(arguments -> minecart(arguments).getMaxSpeed()));
        builder.extension(Minecart.class, "setMaxSpeed", function -> function.param("speed", Double.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    minecart(arguments).setMaxSpeed(NovaTypeSupport.argument(arguments, 1, Double.class));
                    return null;
                }));
        builder.extension(Minecart.class, "isSlowWhenEmpty", function -> function.returns(Boolean.class)
                .invoke(arguments -> minecart(arguments).isSlowWhenEmpty()));
        builder.extension(Minecart.class, "setSlowWhenEmpty", function -> function.param("slow", Boolean.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    minecart(arguments).setSlowWhenEmpty(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(Minecart.class, "flyingVelocityMod", function -> function.returns(Vector.class)
                .invoke(arguments -> minecart(arguments).getFlyingVelocityMod()));
        builder.extension(Minecart.class, "setFlyingVelocityMod", function -> function.param("velocity", Vector.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    minecart(arguments).setFlyingVelocityMod(NovaTypeSupport.argument(arguments, 1, Vector.class));
                    return null;
                }));
        builder.extension(Minecart.class, "derailedVelocityMod", function -> function.returns(Vector.class)
                .invoke(arguments -> minecart(arguments).getDerailedVelocityMod()));
        builder.extension(Minecart.class, "setDerailedVelocityMod", function -> function.param("velocity", Vector.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    minecart(arguments).setDerailedVelocityMod(NovaTypeSupport.argument(arguments, 1, Vector.class));
                    return null;
                }));
        builder.extension(Minecart.class, "displayBlock", function -> function.returns(MaterialData.class)
                .invoke(arguments -> minecart(arguments).getDisplayBlock()));
        builder.extension(Minecart.class, "setDisplayBlock", function -> function.param("data", MaterialData.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    minecart(arguments).setDisplayBlock(NovaTypeSupport.argument(arguments, 1, MaterialData.class));
                    return null;
                }));
        builder.extension(Minecart.class, "displayBlockOffset", function -> function.returns(Integer.class)
                .invoke(arguments -> minecart(arguments).getDisplayBlockOffset()));
        builder.extension(Minecart.class, "setDisplayBlockOffset", function -> function.param("offset", Integer.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    minecart(arguments).setDisplayBlockOffset(NovaTypeSupport.argument(arguments, 1, Integer.class));
                    return null;
                }));
    }

    private static Minecart minecart(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Minecart.class);
    }
}
