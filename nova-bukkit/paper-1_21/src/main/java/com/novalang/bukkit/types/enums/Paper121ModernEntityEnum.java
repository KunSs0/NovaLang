package com.novalang.bukkit.types.enums;

import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;

/** Paper 1.21.x 可用实体子类型的全局查询入口。 */
public final class Paper121ModernEntityEnum {

    private Paper121ModernEntityEnum() {
    }

    /** 注册 Paper 1.21.x 仍公开的实体子类型查询函数。 */
    public static void register(JavaTypes.Builder builder) {
        Paper121EnumSupport.registerEnum(builder, "horseColor", Horse.Color.class);
        Paper121EnumSupport.registerEnum(builder, "horseStyle", Horse.Style.class);
        Paper121EnumSupport.registerEnum(builder, "horseVariant", Horse.Variant.class);
        Paper121EnumSupport.registerEnum(builder, "llamaColor", Llama.Color.class);
        Paper121EnumSupport.registerEnum(builder, "parrotVariant", Parrot.Variant.class);
        Paper121EnumSupport.registerEnum(builder, "ocelotType", Ocelot.Type.class);
        Paper121EnumSupport.registerEnum(builder, "rabbitType", Rabbit.Type.class);
        Paper121EnumSupport.registerEnum(builder, "skeletonSkeletonType", Skeleton.SkeletonType.class);
        Paper121EnumSupport.registerOldEnum(builder, "villagerProfession", Villager.Profession.class, Villager.Profession::valueOf);
    }
}
