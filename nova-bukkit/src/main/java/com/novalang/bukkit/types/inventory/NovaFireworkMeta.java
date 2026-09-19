package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkMeta;

/** 烟花物品元数据的可选编译期别名。 */
@Requires(classes = {"org.bukkit.inventory.meta.FireworkMeta"})
public final class NovaFireworkMeta {

    private NovaFireworkMeta() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(FireworkMeta.class, "addEffect", function -> function
                .param("effect", FireworkEffect.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).addEffect(argument(arguments, 1, FireworkEffect.class));
                    return null;
                }));
        builder.extension(FireworkMeta.class, "effects", function -> function
                .returns(JavaTypeRef.listOf(JavaTypeRef.javaType(FireworkEffect.class)))
                .invoke(arguments -> meta(arguments).getEffects()));
        builder.extension(FireworkMeta.class, "effectsSize", function -> function
                .returns(Integer.class)
                .invoke(arguments -> meta(arguments).getEffectsSize()));
        builder.extension(FireworkMeta.class, "removeEffect", function -> function
                .param("index", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).removeEffect(argument(arguments, 1, Integer.class));
                    return null;
                }));
        builder.extension(FireworkMeta.class, "clearEffects", function -> function
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).clearEffects();
                    return null;
                }));
        builder.extension(FireworkMeta.class, "hasEffects", function -> function
                .returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasEffects()));
        builder.extension(FireworkMeta.class, "power", function -> function
                .returns(Integer.class)
                .invoke(arguments -> meta(arguments).getPower()));
        builder.extension(FireworkMeta.class, "setPower", function -> function
                .param("power", Integer.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    meta(arguments).setPower(argument(arguments, 1, Integer.class));
                    return null;
                }));
        builder.extension(FireworkMeta.class, "clone", function -> function
                .returns(FireworkMeta.class)
                .invoke(arguments -> meta(arguments).clone()));
    }

    private static FireworkMeta meta(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, FireworkMeta.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
