package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Hopper;

/** 旧版 Hopper 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Hopper"})
final class NovaLegacyHopper {

    private NovaLegacyHopper() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Hopper.class, "setActive", function -> function.param("active", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { hopper(arguments).setActive(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(Hopper.class, "isActive", function -> function.returns(Boolean.class)
                .invoke(arguments -> hopper(arguments).isActive()));
        builder.extension(Hopper.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { hopper(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Hopper.class, "facing", function -> function.returns(BlockFace.class)
                .invoke(arguments -> hopper(arguments).getFacing()));
        builder.extension(Hopper.class, "toString", function -> function.returns(String.class)
                .invoke(arguments -> hopper(arguments).toString()));
        builder.extension(Hopper.class, "clone", function -> function.returns(Hopper.class)
                .invoke(arguments -> hopper(arguments).clone()));
        builder.extension(Hopper.class, "isPowered", function -> function.returns(Boolean.class)
                .invoke(arguments -> hopper(arguments).isPowered()));
    }

    private static Hopper hopper(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Hopper.class);
    }
}
