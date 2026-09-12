package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Ladder;

/** 旧版 Ladder 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Ladder"})
final class NovaLegacyLadder {

    private NovaLegacyLadder() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Ladder.class, "attachedFace", function -> function.returns(BlockFace.class).invoke(arguments -> ladder(arguments).getAttachedFace()));
        builder.extension(Ladder.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { ladder(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Ladder.class, "clone", function -> function.returns(Ladder.class).invoke(arguments -> ladder(arguments).clone()));
    }

    private static Ladder ladder(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Ladder.class);
    }
}
