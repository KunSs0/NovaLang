package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Torch;

/** 旧版 Torch 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Torch"})
final class NovaLegacyTorch {

    private NovaLegacyTorch() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Torch.class, "attachedFace", function -> function.returns(BlockFace.class).invoke(arguments -> torch(arguments).getAttachedFace()));
        builder.extension(Torch.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { torch(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Torch.class, "clone", function -> function.returns(Torch.class).invoke(arguments -> torch(arguments).clone()));
    }

    private static Torch torch(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Torch.class);
    }
}
