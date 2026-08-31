package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Dispenser;

/** 旧版 Dispenser 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Dispenser"})
final class NovaLegacyDispenserMaterial {

    private NovaLegacyDispenserMaterial() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Dispenser.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { dispenser(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Dispenser.class, "facing", function -> function.returns(BlockFace.class)
                .invoke(arguments -> dispenser(arguments).getFacing()));
        builder.extension(Dispenser.class, "clone", function -> function.returns(Dispenser.class)
                .invoke(arguments -> dispenser(arguments).clone()));
    }

    private static Dispenser dispenser(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Dispenser.class);
    }
}
