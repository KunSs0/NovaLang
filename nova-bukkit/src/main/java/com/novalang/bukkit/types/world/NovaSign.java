package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.Sign;

/** Sign 方块状态在 Spigot 1.12.2 可用的 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.block.Sign"})
final class NovaSign {

    private NovaSign() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(Sign.class, "lines", function -> function.returns(String[].class)
                .invoke(arguments -> sign(arguments).getLines()));
        builder.extension(Sign.class, "getLine", function -> function.param("index", Integer.class)
                .returns(String.class).invoke(arguments -> sign(arguments)
                        .getLine(NovaTypeSupport.argument(arguments, 1, Integer.class))));
        builder.extension(Sign.class, "setLine", function -> function.param("index", Integer.class)
                .param("text", String.class).returns(Void.TYPE).invoke(arguments -> {
                    sign(arguments).setLine(NovaTypeSupport.argument(arguments, 1, Integer.class),
                            NovaTypeSupport.argument(arguments, 2, String.class));
                    return null;
                }));
    }

    private static Sign sign(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, Sign.class);
    }
}
