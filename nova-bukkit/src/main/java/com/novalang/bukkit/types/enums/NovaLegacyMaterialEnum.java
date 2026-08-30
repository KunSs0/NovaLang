package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.types.MushroomBlockTexture;

/** Spigot 1.12.2 旧版 MaterialData 枚举全局函数。 */
@Requires(classes = {
        "org.bukkit.material.CocoaPlant$CocoaPlantSize",
        "org.bukkit.material.types.MushroomBlockTexture"
})
public final class NovaLegacyMaterialEnum {
    private NovaLegacyMaterialEnum() {
    }

    public static void register(JavaTypes.Builder builder) {
        NovaEnum.registerEnum(builder, "cocoaPlantCocoaPlantSize", CocoaPlant.CocoaPlantSize.class);
        NovaEnum.registerEnum(builder, "mushroomBlockTexture", MushroomBlockTexture.class);
    }
}
