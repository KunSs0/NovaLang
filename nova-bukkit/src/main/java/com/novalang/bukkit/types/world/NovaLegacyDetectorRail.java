package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.DetectorRail;

/** 旧版 DetectorRail 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.DetectorRail"})
final class NovaLegacyDetectorRail {

    private NovaLegacyDetectorRail() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(DetectorRail.class, "isPressed", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> rail(arguments).isPressed()));
        builder.extension(DetectorRail.class, "setPressed", function -> function
                .param("pressed", Boolean.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    rail(arguments).setPressed(NovaTypeSupport.argument(arguments, 1, Boolean.class));
                    return null;
                }));
        builder.extension(DetectorRail.class, "clone", function -> function
                .returns(DetectorRail.class)
                .invoke(arguments -> rail(arguments).clone()));
    }

    private static DetectorRail rail(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, DetectorRail.class);
    }
}
