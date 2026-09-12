package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Attachable;

/** 旧版 Attachable 材料状态的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Attachable"})
final class NovaLegacyAttachable {

    private NovaLegacyAttachable() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Attachable.class, "attachedFace", function -> function
                .returns(BlockFace.class)
                .invoke(arguments -> NovaTypeSupport.argument(arguments, 0, Attachable.class).getAttachedFace()));
    }
}
