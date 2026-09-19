package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.inventory.InventoryType;

/** Spigot 1.12.2 库存类型的 Fluxon 函数别名。 */
public final class NovaInventoryType {

    private NovaInventoryType() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(InventoryType.class, "defaultSize", function -> function.returns(Integer.class)
                .invoke(arguments -> type(arguments).getDefaultSize()));
        builder.extension(InventoryType.class, "defaultTitle", function -> function.returns(String.class)
                .invoke(arguments -> type(arguments).getDefaultTitle()));
    }

    private static InventoryType type(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, InventoryType.class);
    }
}
