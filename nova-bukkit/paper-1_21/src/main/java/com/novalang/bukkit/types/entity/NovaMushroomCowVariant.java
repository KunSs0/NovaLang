package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.paper.entity.PaperEntityReflection;
import com.novalang.runtime.host.JavaTypes;

/** MushroomCow.Variant 的 Fluxon 枚举查询入口。 */
@Requires(classes = {"org.bukkit.entity.MushroomCow$Variant"})
public final class NovaMushroomCowVariant {
    private NovaMushroomCowVariant() { }
    public static void register(JavaTypes.Builder builder) {
        PaperEntityReflection.registerEnum(builder, "mushroomCowVariant", PaperEntityReflection.type(NovaMushroomCowVariant.class, "org.bukkit.entity.MushroomCow$Variant"));
    }
}
