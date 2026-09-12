package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Bed;

/** 旧版 Bed 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Bed"})
final class NovaLegacyBed {

    private NovaLegacyBed() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Bed.class, "isHeadOfBed", function -> function.returns(Boolean.class).invoke(arguments -> bed(arguments).isHeadOfBed()));
        builder.extension(Bed.class, "setHeadOfBed", function -> function.param("head", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { bed(arguments).setHeadOfBed(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(Bed.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { bed(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Bed.class, "facing", function -> function.returns(BlockFace.class).invoke(arguments -> bed(arguments).getFacing()));
        builder.extension(Bed.class, "toString", function -> function.returns(String.class).invoke(arguments -> bed(arguments).toString()));
        builder.extension(Bed.class, "clone", function -> function.returns(Bed.class).invoke(arguments -> bed(arguments).clone()));
    }

    private static Bed bed(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Bed.class);
    }
}
