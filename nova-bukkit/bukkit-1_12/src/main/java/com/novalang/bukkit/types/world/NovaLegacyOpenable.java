package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.Openable;

/** 旧版 Openable 材料状态的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Openable"})
final class NovaLegacyOpenable {

    private NovaLegacyOpenable() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Openable.class, "isOpen", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> openable(arguments).isOpen()));
        builder.extension(Openable.class, "setOpen", function -> function
                .param("open", Boolean.class)
                .invoke(arguments -> {
                    openable(arguments).setOpen(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
    }

    private static Openable openable(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Openable.class);
    }
}
