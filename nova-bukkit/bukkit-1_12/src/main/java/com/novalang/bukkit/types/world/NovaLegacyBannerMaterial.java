package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Banner;

/** 旧版 Banner 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Banner"})
final class NovaLegacyBannerMaterial {

    private NovaLegacyBannerMaterial() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Banner.class, "isWallBanner", function -> function.returns(Boolean.class).invoke(arguments -> banner(arguments).isWallBanner()));
        builder.extension(Banner.class, "attachedFace", function -> function.returns(BlockFace.class).invoke(arguments -> banner(arguments).getAttachedFace()));
        builder.extension(Banner.class, "facing", function -> function.returns(BlockFace.class).invoke(arguments -> banner(arguments).getFacing()));
        builder.extension(Banner.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { banner(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Banner.class, "toString", function -> function.returns(String.class).invoke(arguments -> banner(arguments).toString()));
        builder.extension(Banner.class, "clone", function -> function.returns(Banner.class).invoke(arguments -> banner(arguments).clone()));
    }

    private static Banner banner(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Banner.class);
    }
}
