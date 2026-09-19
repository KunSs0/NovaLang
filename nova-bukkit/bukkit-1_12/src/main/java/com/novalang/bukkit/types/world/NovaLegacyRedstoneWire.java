package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.RedstoneWire;

/** 旧版 RedstoneWire 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.RedstoneWire"})
final class NovaLegacyRedstoneWire {

    private NovaLegacyRedstoneWire() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(RedstoneWire.class, "isPowered", function -> function.returns(Boolean.class).invoke(arguments -> wire(arguments).isPowered()));
        builder.extension(RedstoneWire.class, "toString", function -> function.returns(String.class).invoke(arguments -> wire(arguments).toString()));
        builder.extension(RedstoneWire.class, "clone", function -> function.returns(RedstoneWire.class).invoke(arguments -> wire(arguments).clone()));
    }

    private static RedstoneWire wire(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, RedstoneWire.class);
    }
}
