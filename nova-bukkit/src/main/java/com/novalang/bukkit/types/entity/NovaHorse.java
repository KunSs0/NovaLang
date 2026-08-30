package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.HorseInventory;

/** Horse 在 Spigot 1.12.2 可用的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.entity.Horse"})
final class NovaHorse {

    private NovaHorse() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Horse.class, "color", function -> function.returns(Horse.Color.class)
                .invoke(arguments -> horse(arguments).getColor()));
        builder.extension(Horse.class, "setColor", function -> function.param("color", Horse.Color.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    horse(arguments).setColor(NovaTypeSupport.argument(arguments, 1, Horse.Color.class));
                    return null;
                }));
        builder.extension(Horse.class, "style", function -> function.returns(Horse.Style.class)
                .invoke(arguments -> horse(arguments).getStyle()));
        builder.extension(Horse.class, "setStyle", function -> function.param("style", Horse.Style.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    horse(arguments).setStyle(NovaTypeSupport.argument(arguments, 1, Horse.Style.class));
                    return null;
                }));
        builder.extension(Horse.class, "isCarryingChest", function -> function.returns(Boolean.class)
                .invoke(arguments -> horse(arguments).isCarryingChest()));
        builder.extension(Horse.class, "setCarryingChest", function -> function.param("carrying", Boolean.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    horse(arguments).setCarryingChest(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(Horse.class, "inventory", function -> function.returns(HorseInventory.class)
                .invoke(arguments -> horse(arguments).getInventory()));
    }

    private static Horse horse(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Horse.class);
    }
}
