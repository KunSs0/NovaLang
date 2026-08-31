package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Skull;

/** 旧版 Skull 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Skull"})
final class NovaLegacySkullMaterial {

    private NovaLegacySkullMaterial() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Skull.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { skull(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Skull.class, "facing", function -> function.returns(BlockFace.class).invoke(arguments -> skull(arguments).getFacing()));
        builder.extension(Skull.class, "toString", function -> function.returns(String.class).invoke(arguments -> skull(arguments).toString()));
        builder.extension(Skull.class, "clone", function -> function.returns(Skull.class).invoke(arguments -> skull(arguments).clone()));
    }

    private static Skull skull(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Skull.class);
    }
}
