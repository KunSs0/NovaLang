package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.material.Command;

/** 旧版 Command 材料数据的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.material.Command"})
final class NovaLegacyCommandMaterial {

    private NovaLegacyCommandMaterial() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Command.class, "isPowered", function -> function.returns(Boolean.class).invoke(arguments -> command(arguments).isPowered()));
        builder.extension(Command.class, "setPowered", function -> function.param("powered", Boolean.class).returns(Void.TYPE)
                .invoke(arguments -> { command(arguments).setPowered(NovaTypeSupport.argument(arguments, 1, Boolean.class)); return null; }));
        builder.extension(Command.class, "toString", function -> function.returns(String.class).invoke(arguments -> command(arguments).toString()));
        builder.extension(Command.class, "clone", function -> function.returns(Command.class).invoke(arguments -> command(arguments).clone()));
    }

    private static Command command(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Command.class);
    }
}
