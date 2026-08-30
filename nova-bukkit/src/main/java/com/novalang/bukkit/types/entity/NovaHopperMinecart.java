package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.minecart.HopperMinecart;

/** Spigot 1.12.2 中漏斗矿车的 Fluxon 函数别名。 */
@Requires(classes = {"org.bukkit.entity.minecart.HopperMinecart"})
public final class NovaHopperMinecart {

    private NovaHopperMinecart() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(HopperMinecart.class, "isEnabled", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> hopperMinecart(arguments).isEnabled()));
        builder.extension(HopperMinecart.class, "setEnabled", function -> function
                .param("enabled", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    hopperMinecart(arguments).setEnabled(
                            NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
    }

    private static HopperMinecart hopperMinecart(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, HopperMinecart.class);
    }
}
