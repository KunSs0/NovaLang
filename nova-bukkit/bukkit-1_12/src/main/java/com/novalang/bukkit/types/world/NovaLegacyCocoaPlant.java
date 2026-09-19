package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.CocoaPlant;

/** 旧版 CocoaPlant 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.CocoaPlant"})
final class NovaLegacyCocoaPlant {

    private NovaLegacyCocoaPlant() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(CocoaPlant.class, "size", function -> function.returns(CocoaPlant.CocoaPlantSize.class).invoke(arguments -> plant(arguments).getSize()));
        builder.extension(CocoaPlant.class, "setSize", function -> function.param("size", CocoaPlant.CocoaPlantSize.class).returns(Void.TYPE)
                .invoke(arguments -> { plant(arguments).setSize(NovaTypeSupport.argument(arguments, 1, CocoaPlant.CocoaPlantSize.class)); return null; }));
        builder.extension(CocoaPlant.class, "setSize", function -> function.param("size", String.class).returns(Void.TYPE)
                .invoke(arguments -> { setNamedSize(plant(arguments), NovaTypeSupport.argument(arguments, 1, String.class)); return null; }));
        builder.extension(CocoaPlant.class, "attachedFace", function -> function.returns(BlockFace.class).invoke(arguments -> plant(arguments).getAttachedFace()));
        builder.extension(CocoaPlant.class, "setFacingDirection", function -> function.param("face", BlockFace.class).returns(Void.TYPE)
                .invoke(arguments -> { plant(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class)); return null; }));
        builder.extension(CocoaPlant.class, "facing", function -> function.returns(BlockFace.class).invoke(arguments -> plant(arguments).getFacing()));
        builder.extension(CocoaPlant.class, "clone", function -> function.returns(CocoaPlant.class).invoke(arguments -> plant(arguments).clone()));
        builder.extension(CocoaPlant.class, "toString", function -> function.returns(String.class).invoke(arguments -> plant(arguments).toString()));
    }

    private static void setNamedSize(CocoaPlant plant, String value) {
        CocoaPlant.CocoaPlantSize size = NovaTypeSupport.findEnum(CocoaPlant.CocoaPlantSize.class, value);
        if (size != null) {
            plant.setSize(size);
        }
    }

    private static CocoaPlant plant(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, CocoaPlant.class);
    }
}
