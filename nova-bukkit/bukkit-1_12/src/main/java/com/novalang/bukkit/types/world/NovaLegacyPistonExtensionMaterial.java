package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.PistonExtensionMaterial;

/** 旧版 PistonExtensionMaterial 的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.PistonExtensionMaterial"})
final class NovaLegacyPistonExtensionMaterial {

    private NovaLegacyPistonExtensionMaterial() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(PistonExtensionMaterial.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { piston(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(PistonExtensionMaterial.class, "facing", function -> function.returns(BlockFace.class).invoke(arguments -> piston(arguments).getFacing()));
        builder.extension(PistonExtensionMaterial.class, "isSticky", function -> function.returns(Boolean.class).invoke(arguments -> piston(arguments).isSticky()));
        builder.extension(PistonExtensionMaterial.class, "setSticky", function -> function.param("sticky", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { piston(arguments).setSticky(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(PistonExtensionMaterial.class, "attachedFace", function -> function.returns(BlockFace.class).invoke(arguments -> piston(arguments).getAttachedFace()));
        builder.extension(PistonExtensionMaterial.class, "clone", function -> function.returns(PistonExtensionMaterial.class).invoke(arguments -> piston(arguments).clone()));
    }

    private static PistonExtensionMaterial piston(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PistonExtensionMaterial.class);
    }
}
