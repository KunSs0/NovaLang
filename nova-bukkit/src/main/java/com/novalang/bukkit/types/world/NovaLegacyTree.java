package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Tree;

/** 旧版 Tree 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Tree"})
final class NovaLegacyTree {

    private NovaLegacyTree() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Tree.class, "direction", function -> function.returns(BlockFace.class).invoke(arguments -> tree(arguments).getDirection()));
        builder.extension(Tree.class, "setDirection", function -> function.param("direction", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { tree(arguments).setDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Tree.class, "toString", function -> function.returns(String.class).invoke(arguments -> tree(arguments).toString()));
        builder.extension(Tree.class, "clone", function -> function.returns(Tree.class).invoke(arguments -> tree(arguments).clone()));
    }

    private static Tree tree(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Tree.class);
    }
}
