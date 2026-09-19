package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.SimpleAttachableMaterialData;

/** 旧版 SimpleAttachableMaterialData 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.SimpleAttachableMaterialData"})
final class NovaLegacySimpleAttachableMaterialData {

    private NovaLegacySimpleAttachableMaterialData() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(SimpleAttachableMaterialData.class, "facing", function -> function
                .returns(BlockFace.class)
                .invoke(arguments -> material(arguments).getFacing()));
        builder.extension(SimpleAttachableMaterialData.class, "toString", function -> function
                .returns(String.class)
                .invoke(arguments -> material(arguments).toString()));
        builder.extension(SimpleAttachableMaterialData.class, "clone", function -> function
                .returns(SimpleAttachableMaterialData.class)
                .invoke(arguments -> material(arguments).clone()));
    }

    private static SimpleAttachableMaterialData material(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, SimpleAttachableMaterialData.class);
    }
}
