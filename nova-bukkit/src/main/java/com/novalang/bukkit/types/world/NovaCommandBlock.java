package com.novalang.bukkit.types.world;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.block.CommandBlock;

/** CommandBlock 方块状态的 Spigot 1.12.2 Fluxon 别名。 */
@Requires(classes = {"org.bukkit.block.CommandBlock"})
final class NovaCommandBlock {

    private NovaCommandBlock() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(CommandBlock.class, "command", function -> function.returns(String.class)
                .invoke(arguments -> commandBlock(arguments).getCommand()));
        builder.extension(CommandBlock.class, "setCommand", function -> function.param("command", String.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    commandBlock(arguments).setCommand(NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(CommandBlock.class, "name", function -> function.returns(String.class)
                .invoke(arguments -> commandBlock(arguments).getName()));
        builder.extension(CommandBlock.class, "setName", function -> function.param("name", String.class)
                .returns(Void.TYPE).invoke(arguments -> {
                    commandBlock(arguments).setName(NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
    }

    private static CommandBlock commandBlock(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, CommandBlock.class);
    }
}
