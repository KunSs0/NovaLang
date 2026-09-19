package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.PistonMoveReaction;

/** Spigot 1.12.2 PistonMoveReaction 的 Fluxon 函数别名。 */
@SuppressWarnings("deprecation")
public final class NovaPistonMoveReaction {

    private NovaPistonMoveReaction() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PistonMoveReaction.class, "id", function -> function
                .returns(Integer.class)
                .invoke(arguments -> pistonMoveReaction(arguments).getId()));
    }

    private static PistonMoveReaction pistonMoveReaction(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PistonMoveReaction.class);
    }
}
