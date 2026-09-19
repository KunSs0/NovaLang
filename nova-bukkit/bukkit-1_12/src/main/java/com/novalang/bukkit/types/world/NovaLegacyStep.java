package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.Step;

import java.util.List;

/** 旧版 Step 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Step"})
final class NovaLegacyStep {

    private NovaLegacyStep() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Step.class, "textures", function -> function.returns(List.class).invoke(arguments -> step(arguments).getTextures()));
        builder.extension(Step.class, "isInverted", function -> function.returns(Boolean.class).invoke(arguments -> step(arguments).isInverted()));
        builder.extension(Step.class, "setInverted", function -> function.param("inverted", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { step(arguments).setInverted(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(Step.class, "clone", function -> function.returns(Step.class).invoke(arguments -> step(arguments).clone()));
        builder.extension(Step.class, "toString", function -> function.returns(String.class).invoke(arguments -> step(arguments).toString()));
    }

    private static Step step(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Step.class);
    }
}
