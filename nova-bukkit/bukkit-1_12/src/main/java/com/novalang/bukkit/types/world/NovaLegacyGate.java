package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Gate;

/** 旧版 Gate 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Gate"})
final class NovaLegacyGate {

    private NovaLegacyGate() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Gate.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { gate(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Gate.class, "facing", function -> function.returns(BlockFace.class)
                .invoke(arguments -> gate(arguments).getFacing()));
        builder.extension(Gate.class, "isOpen", function -> function.returns(Boolean.class)
                .invoke(arguments -> gate(arguments).isOpen()));
        builder.extension(Gate.class, "setOpen", function -> function.param("open", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { gate(arguments).setOpen(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(Gate.class, "toString", function -> function.returns(String.class)
                .invoke(arguments -> gate(arguments).toString()));
        builder.extension(Gate.class, "clone", function -> function.returns(Gate.class)
                .invoke(arguments -> gate(arguments).clone()));
    }

    private static Gate gate(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Gate.class);
    }
}
