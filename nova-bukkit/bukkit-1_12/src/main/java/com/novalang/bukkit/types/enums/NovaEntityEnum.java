package com.novalang.bukkit.types.enums;

import com.novalang.bukkit.Requires;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;

/** 实体子类型的 Spigot 1.12.2 Fluxon 枚举入口。 */
@SuppressWarnings("deprecation")
@Requires(classes = {"org.bukkit.entity.Arrow$PickupStatus", "org.bukkit.entity.Villager$Career"})
final class NovaEntityEnum {

    private NovaEntityEnum() {
    }

    static void register(JavaTypes.Builder builder) {
        Bukkit112EnumSupport.registerEnum(builder, "arrowPickupStatus", Arrow.PickupStatus.class);
        Bukkit112EnumSupport.registerEnum(builder, "horseColor", Horse.Color.class);
        Bukkit112EnumSupport.registerEnum(builder, "horseStyle", Horse.Style.class);
        Bukkit112EnumSupport.registerEnum(builder, "horseVariant", Horse.Variant.class);
        Bukkit112EnumSupport.registerEnum(builder, "llamaColor", Llama.Color.class);
        Bukkit112EnumSupport.registerEnum(builder, "parrotVariant", Parrot.Variant.class);
        Bukkit112EnumSupport.registerEnum(builder, "ocelotType", Ocelot.Type.class);
        Bukkit112EnumSupport.registerEnum(builder, "rabbitType", Rabbit.Type.class);
        Bukkit112EnumSupport.registerEnum(builder, "skeletonSkeletonType", Skeleton.SkeletonType.class);
        Bukkit112EnumSupport.registerEnum(builder, "villagerProfession", Villager.Profession.class);
        Bukkit112EnumSupport.registerEnum(builder, "villagerCareer", Villager.Career.class);
    }
}
