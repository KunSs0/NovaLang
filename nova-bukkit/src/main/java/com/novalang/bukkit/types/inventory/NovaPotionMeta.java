package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Color;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/** 药水物品元数据的可选编译期别名。 */
@Requires(classes = {"org.bukkit.inventory.meta.PotionMeta"})
@SuppressWarnings("deprecation")
public final class NovaPotionMeta {

    private NovaPotionMeta() {
    }

    public static void register(JavaTypes.Builder builder) {
        JavaTypeRef nullableColor = JavaTypeRef.javaType(Color.class).nullable();
        builder.extension(PotionMeta.class, "basePotionData", function -> function
                .returns(PotionData.class)
                .invoke(arguments -> meta(arguments).getBasePotionData()));
        builder.extension(PotionMeta.class, "setBasePotionData", function -> function
                .param("data", PotionData.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setBasePotionData(argument(arguments, 1, PotionData.class));
                    return null;
                }));
        builder.extension(PotionMeta.class, "hasCustomEffects", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasCustomEffects()));
        builder.extension(PotionMeta.class, "customEffects", function -> function
                .returns(JavaTypeRef.listOf(JavaTypeRef.javaType(PotionEffect.class)))
                .invoke(arguments -> meta(arguments).getCustomEffects()));
        builder.extension(PotionMeta.class, "addCustomEffect", function -> function
                .param("effect", PotionEffect.class)
                .param("overwrite", Boolean.class)
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).addCustomEffect(argument(arguments, 1, PotionEffect.class), argument(arguments, 2, Boolean.class))));
        builder.extension(PotionMeta.class, "removeCustomEffect", function -> function
                .param("type", PotionEffectType.class)
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).removeCustomEffect(argument(arguments, 1, PotionEffectType.class))));
        builder.extension(PotionMeta.class, "hasCustomEffect", function -> function
                .param("type", PotionEffectType.class)
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasCustomEffect(argument(arguments, 1, PotionEffectType.class))));
        builder.extension(PotionMeta.class, "clearCustomEffects", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).clearCustomEffects()));
        builder.extension(PotionMeta.class, "hasColor", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasColor()));
        builder.extension(PotionMeta.class, "color", function -> function
                .returns(nullableColor)
                .invoke(arguments -> meta(arguments).getColor()));
        builder.extension(PotionMeta.class, "setColor", function -> function
                .param("color", nullableColor)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setColor(argument(arguments, 1, Color.class));
                    return null;
                }));
        builder.extension(PotionMeta.class, "clone", function -> function
                .returns(PotionMeta.class)
                .invoke(arguments -> meta(arguments).clone()));
    }

    private static PotionMeta meta(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PotionMeta.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
