package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.event.inventory.ClickType;

/** Spigot 1.12.2 ClickType 的 Fluxon 函数别名。 */
public final class NovaClickType {

    private NovaClickType() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(ClickType.class, "isKeyboardClick", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> clickType(arguments).isKeyboardClick()));
        builder.extension(ClickType.class, "isCreativeAction", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> clickType(arguments).isCreativeAction()));
        builder.extension(ClickType.class, "isRightClick", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> clickType(arguments).isRightClick()));
        builder.extension(ClickType.class, "isLeftClick", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> clickType(arguments).isLeftClick()));
        builder.extension(ClickType.class, "isShiftClick", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> clickType(arguments).isShiftClick()));
    }

    private static ClickType clickType(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, ClickType.class);
    }
}
