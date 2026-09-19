package com.novalang.bukkit.types.gameplay;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeWrapper;

/** Spigot 1.12.2 PotionEffectTypeWrapper 扩展。 */
@Requires(classes = {"org.bukkit.potion.PotionEffectTypeWrapper"})
public final class NovaPotionEffectTypeWrapper {

    private NovaPotionEffectTypeWrapper() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(PotionEffectTypeWrapper.class, "type", function -> function.returns(PotionEffectType.class).invoke(arguments -> wrapper(arguments).getType()));
    }

    private static PotionEffectTypeWrapper wrapper(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, PotionEffectTypeWrapper.class);
    }
}
