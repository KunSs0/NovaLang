package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Mushroom;
import org.bukkit.material.types.MushroomBlockTexture;

/** 旧版 Mushroom 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Mushroom", "org.bukkit.material.types.MushroomBlockTexture"})
final class NovaLegacyMushroom {

    private NovaLegacyMushroom() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Mushroom.class, "isStem", function -> function.returns(Boolean.class).invoke(arguments -> mushroom(arguments).isStem()));
        builder.extension(Mushroom.class, "setStem", function -> function.returns(Void.TYPE).invoke(arguments -> { mushroom(arguments).setStem(); return null; }));
        builder.extension(Mushroom.class, "blockTexture", function -> function.returns(MushroomBlockTexture.class).invoke(arguments -> mushroom(arguments).getBlockTexture()));
        builder.extension(Mushroom.class, "setBlockTexture", function -> function.param("texture", MushroomBlockTexture.class).returns(Void.TYPE)
                .invoke(arguments -> { mushroom(arguments).setBlockTexture(NovaTypeSupport.argument(arguments, 1, MushroomBlockTexture.class)); return null; }));
        builder.extension(Mushroom.class, "isFacePainted", function -> function.param("face", BlockFace.class).returns(Boolean.class)
                .invoke(arguments -> mushroom(arguments).isFacePainted(NovaTypeSupport.argument(arguments, 1, BlockFace.class))));
        builder.extension(Mushroom.class, "setFacePainted", function -> function.param("face", BlockFace.class).param("painted", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { mushroom(arguments).setFacePainted(NovaTypeSupport.argument(arguments, 1, BlockFace.class), NovaTypeSupport.argument(arguments, 2, Boolean.class)); return null; }));
        builder.extension(Mushroom.class, "paintedFaces", function -> function.returns(JavaTypeRef.setOf(JavaTypeRef.javaType(BlockFace.class)))
                .invoke(arguments -> mushroom(arguments).getPaintedFaces()));
        builder.extension(Mushroom.class, "toString", function -> function.returns(String.class).invoke(arguments -> mushroom(arguments).toString()));
        builder.extension(Mushroom.class, "clone", function -> function.returns(Mushroom.class).invoke(arguments -> mushroom(arguments).clone()));
    }

    private static Mushroom mushroom(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Mushroom.class);
    }
}
