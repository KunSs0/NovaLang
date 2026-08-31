package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.DirectionalContainer;

/** 旧版 DirectionalContainer 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.DirectionalContainer"})
final class NovaLegacyDirectionalContainer {

    private NovaLegacyDirectionalContainer() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(DirectionalContainer.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { container(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(DirectionalContainer.class, "facing", function -> function.returns(BlockFace.class)
                .invoke(arguments -> container(arguments).getFacing()));
        builder.extension(DirectionalContainer.class, "toString", function -> function.returns(String.class)
                .invoke(arguments -> container(arguments).toString()));
        builder.extension(DirectionalContainer.class, "clone", function -> function.returns(DirectionalContainer.class)
                .invoke(arguments -> container(arguments).clone()));
    }

    private static DirectionalContainer container(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, DirectionalContainer.class);
    }
}
