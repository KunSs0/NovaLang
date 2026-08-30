package com.novalang.bukkit.types.world;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.PressurePlate;

/** Spigot 1.12.2 旧版压力板材料数据的 Fluxon 函数别名。 */
public final class NovaLegacyPressurePlate {

    private NovaLegacyPressurePlate() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PressurePlate.class, "isPressed", function -> function.returns(Boolean.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, PressurePlate.class).isPressed()));
    }
}
