package com.novalang.bukkit.types.entity;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.entity.minecart.CommandMinecart;

/** Spigot 1.12.2 中命令方块矿车的 Fluxon 函数别名。 */
@Requires(classes = {"org.bukkit.entity.minecart.CommandMinecart"})
public final class NovaCommandMinecart {

    private NovaCommandMinecart() {
    }

    public static void register(JavaTypes.Builder builder) {
        builder.extension(CommandMinecart.class, "command", function -> function
                .returns(String.class)
                .invoke(arguments -> commandMinecart(arguments).getCommand()));
        builder.extension(CommandMinecart.class, "setCommand", function -> function
                .param("command", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    commandMinecart(arguments).setCommand(
                            NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
        builder.extension(CommandMinecart.class, "setName", function -> function
                .param("name", String.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    commandMinecart(arguments).setName(
                            NovaTypeSupport.argument(arguments, 1, String.class));
                    return null;
                }));
    }

    private static CommandMinecart commandMinecart(Object[] arguments) {
        return NovaTypeSupport.argument(arguments, 0, CommandMinecart.class);
    }
}
