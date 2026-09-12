package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Button;

/** 旧版 Button 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Button"})
final class NovaLegacyButton {

    private NovaLegacyButton() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Button.class, "isPowered", function -> function.returns(Boolean.class)
                .invoke(arguments -> button(arguments).isPowered()));
        builder.extension(Button.class, "setPowered", function -> function.param("powered", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { button(arguments).setPowered(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(Button.class, "attachedFace", function -> function.returns(BlockFace.class)
                .invoke(arguments -> button(arguments).getAttachedFace()));
        builder.extension(Button.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { button(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(Button.class, "toString", function -> function.returns(String.class)
                .invoke(arguments -> button(arguments).toString()));
        builder.extension(Button.class, "clone", function -> function.returns(Button.class)
                .invoke(arguments -> button(arguments).clone()));
    }

    private static Button button(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Button.class);
    }
}
