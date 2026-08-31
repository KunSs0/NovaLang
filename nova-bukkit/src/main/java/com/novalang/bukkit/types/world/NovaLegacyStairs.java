package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Stairs;

/** 旧版 Stairs 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Stairs"})
final class NovaLegacyStairs {

    private NovaLegacyStairs() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Stairs.class, "ascendingDirection", function -> function.returns(BlockFace.class).invoke(arguments -> stairs(arguments).getAscendingDirection()));
        builder.extension(Stairs.class, "descendingDirection", function -> function.returns(BlockFace.class).invoke(arguments -> stairs(arguments).getDescendingDirection()));
        builder.extension(Stairs.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { stairs(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Stairs.class, "facing", function -> function.returns(BlockFace.class).invoke(arguments -> stairs(arguments).getFacing()));
        builder.extension(Stairs.class, "isInverted", function -> function.returns(Boolean.class).invoke(arguments -> stairs(arguments).isInverted()));
        builder.extension(Stairs.class, "setInverted", function -> function.param("inverted", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { stairs(arguments).setInverted(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(Stairs.class, "toString", function -> function.returns(String.class).invoke(arguments -> stairs(arguments).toString()));
        builder.extension(Stairs.class, "clone", function -> function.returns(Stairs.class).invoke(arguments -> stairs(arguments).clone()));
    }

    private static Stairs stairs(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Stairs.class);
    }
}
