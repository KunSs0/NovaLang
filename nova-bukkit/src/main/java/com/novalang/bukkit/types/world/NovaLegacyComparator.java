package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Comparator;

/** 旧版 Comparator 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Comparator"})
final class NovaLegacyComparator {

    private NovaLegacyComparator() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Comparator.class, "setSubtractionMode", function -> function
                .param("subtraction", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    comparator(arguments).setSubtractionMode(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(Comparator.class, "isSubtractionMode", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> comparator(arguments).isSubtractionMode()));
        builder.extension(Comparator.class, "setFacingDirection", function -> function
                .param("face", BlockFace.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    comparator(arguments).setFacingDirection(NovaTypeSupport.argument(arguments, 1, BlockFace.class));
                    return null;
                }));
        builder.extension(Comparator.class, "facing", function -> function
                .returns(BlockFace.class)
                .invoke(arguments -> comparator(arguments).getFacing()));
        builder.extension(Comparator.class, "toString", function -> function
                .returns(String.class)
                .invoke(arguments -> comparator(arguments).toString()));
        builder.extension(Comparator.class, "clone", function -> function
                .returns(Comparator.class)
                .invoke(arguments -> comparator(arguments).clone()));
        builder.extension(Comparator.class, "isPowered", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> comparator(arguments).isPowered()));
        builder.extension(Comparator.class, "isBeingPowered", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> comparator(arguments).isBeingPowered()));
    }

    private static Comparator comparator(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Comparator.class);
    }
}
