package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Rotation;

/** Spigot 1.12.2 Rotation 的 Fluxon 函数别名。 */
public final class NovaRotation {

    private NovaRotation() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Rotation.class, "rotateClockwise", function -> function
                .returns(Rotation.class)
                .invoke(arguments -> rotation(arguments).rotateClockwise()));
        builder.extension(Rotation.class, "rotateCounterClockwise", function -> function
                .returns(Rotation.class)
                .invoke(arguments -> rotation(arguments).rotateCounterClockwise()));
    }

    private static Rotation rotation(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Rotation.class);
    }
}
