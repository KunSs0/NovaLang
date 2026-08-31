package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.WoodenStep;

/** 旧版 WoodenStep 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.WoodenStep"})
final class NovaLegacyWoodenStep {

    private NovaLegacyWoodenStep() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(WoodenStep.class, "isInverted", function -> function.returns(Boolean.class).invoke(arguments -> step(arguments).isInverted()));
        builder.extension(WoodenStep.class, "setInverted", function -> function.param("inverted", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { step(arguments).setInverted(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(WoodenStep.class, "clone", function -> function.returns(WoodenStep.class).invoke(arguments -> step(arguments).clone()));
        builder.extension(WoodenStep.class, "toString", function -> function.returns(String.class).invoke(arguments -> step(arguments).toString()));
    }

    private static WoodenStep step(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, WoodenStep.class);
    }
}
