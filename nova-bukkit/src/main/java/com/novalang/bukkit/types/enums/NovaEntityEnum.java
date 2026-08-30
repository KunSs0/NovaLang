package com.novalang.bukkit.types.enums;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;

/** 实体子类型的 Spigot 1.12.2 Fluxon 枚举入口。 */
@SuppressWarnings("deprecation")
final class NovaEntityEnum {

    private NovaEntityEnum() {
    }

    static void register(JavaTypes.Builder builder) {
        NovaEnum.registerEnum(builder, "horseColor", Horse.Color.class);
        NovaEnum.registerEnum(builder, "horseStyle", Horse.Style.class);
        NovaEnum.registerEnum(builder, "horseVariant", Horse.Variant.class);
        NovaEnum.registerEnum(builder, "llamaColor", Llama.Color.class);
        NovaEnum.registerEnum(builder, "parrotVariant", Parrot.Variant.class);
        NovaEnum.registerEnum(builder, "skeletonSkeletonType", Skeleton.SkeletonType.class);
        NovaEnum.registerEnum(builder, "villagerProfession", Villager.Profession.class);
        NovaEnum.registerEnum(builder, "villagerCareer", Villager.Career.class);
    }
}
