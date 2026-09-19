package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.LightningStrike;

/** Spigot 1.12.2 LightningStrike 扩展。 */
@Requires(classes = {"org.bukkit.entity.LightningStrike"})
public final class NovaLightningStrike {

    private NovaLightningStrike() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(LightningStrike.class, "isEffect", function -> function.returns(Boolean.class).invoke(arguments -> strike(arguments).isEffect()));
    }

    private static LightningStrike strike(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, LightningStrike.class);
    }
}
