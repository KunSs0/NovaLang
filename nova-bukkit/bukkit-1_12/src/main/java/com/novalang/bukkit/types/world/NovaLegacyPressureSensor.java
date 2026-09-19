package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.PressureSensor;

/** 旧版 PressureSensor 材料状态的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.PressureSensor"})
final class NovaLegacyPressureSensor {

    private NovaLegacyPressureSensor() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(PressureSensor.class, "isPressed", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> sensor(arguments).isPressed()));
    }

    private static PressureSensor sensor(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PressureSensor.class);
    }
}
