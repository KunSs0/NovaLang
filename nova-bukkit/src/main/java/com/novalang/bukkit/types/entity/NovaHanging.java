package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Hanging;

/** Spigot 1.12.2 中悬挂实体的 Fluxon 函数别名。 */
@Requires(classes = {"org.bukkit.entity.Hanging"})
public final class NovaHanging {

    private NovaHanging() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(Hanging.class, "setFacingDirection", function -> function
                .param("face", BlockFace.class)
                .param("force", Boolean.class)
                .returns(Boolean.class)
                .invoke(arguments -> hanging(arguments).setFacingDirection(
                        NovaTypeSupport.argument(arguments, 1, BlockFace.class),
                        NovaTypeSupport.argument(arguments, 2, Boolean.class))));
    }

    private static Hanging hanging(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Hanging.class);
    }
}
