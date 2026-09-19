package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Vine;

/** 旧版 Vine 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Vine"})
final class NovaLegacyVine {

    private NovaLegacyVine() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Vine.class, "isOnFace", function -> function.param("face", BlockFace.class).returns(Boolean.class)
                .invoke(arguments -> vine(arguments).isOnFace(NovaTypeSupport.argument(arguments, 1, BlockFace.class))));
        builder.extension(Vine.class, "putOnFace", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { vine(arguments).putOnFace(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Vine.class, "removeFromFace", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { vine(arguments).removeFromFace(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Vine.class, "toString", function -> function.returns(String.class).invoke(arguments -> vine(arguments).toString()));
        builder.extension(Vine.class, "clone", function -> function.returns(Vine.class).invoke(arguments -> vine(arguments).clone()));
    }

    private static Vine vine(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Vine.class);
    }
}
