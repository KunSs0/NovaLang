package com.novalang.bukkit.types.inventory;

import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.FireworkEffect;
import org.bukkit.inventory.meta.FireworkEffectMeta;

/** Spigot 1.12.2 烟花星元数据的 Fluxon 函数别名。 */
public final class NovaFireworkEffectMeta {

    private NovaFireworkEffectMeta() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(FireworkEffectMeta.class, "setEffect", function -> function.param("effect", FireworkEffect.class).returns(Void.TYPE).invoke(arguments -> {
            meta(arguments).setEffect(argument(arguments, 1, FireworkEffect.class));
            return null;
        }));
        builder.extension(FireworkEffectMeta.class, "hasEffect", function -> function.returns(Boolean.class)
                .invoke(arguments -> meta(arguments).hasEffect()));
        builder.extension(FireworkEffectMeta.class, "effect", function -> function.returns(JavaTypeRef.javaType(FireworkEffect.class).nullable())
                .invoke(arguments -> meta(arguments).getEffect()));
    }

    private static FireworkEffectMeta meta(Object[] arguments) {
        return argument(arguments, 0, FireworkEffectMeta.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
