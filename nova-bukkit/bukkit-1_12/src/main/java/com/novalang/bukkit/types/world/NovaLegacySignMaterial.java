package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Sign;

/** 旧版 Sign 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Sign"})
final class NovaLegacySignMaterial {

    private NovaLegacySignMaterial() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Sign.class, "isWallSign", function -> function.returns(Boolean.class).invoke(arguments -> sign(arguments).isWallSign()));
        builder.extension(Sign.class, "attachedFace", function -> function.returns(BlockFace.class).invoke(arguments -> sign(arguments).getAttachedFace()));
        builder.extension(Sign.class, "facing", function -> function.returns(BlockFace.class).invoke(arguments -> sign(arguments).getFacing()));
        builder.extension(Sign.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { sign(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Sign.class, "toString", function -> function.returns(String.class).invoke(arguments -> sign(arguments).toString()));
        builder.extension(Sign.class, "clone", function -> function.returns(Sign.class).invoke(arguments -> sign(arguments).clone()));
    }

    private static Sign sign(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Sign.class);
    }
}
