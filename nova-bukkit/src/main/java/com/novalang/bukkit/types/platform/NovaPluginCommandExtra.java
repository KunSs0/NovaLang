package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

/** PluginCommand 回调绑定的可选 Fluxon 别名。 */
@Requires(classes = {
        "org.bukkit.command.PluginCommand",
        "org.bukkit.command.CommandExecutor",
        "org.bukkit.command.TabCompleter"
})
final class NovaPluginCommandExtra {

    private NovaPluginCommandExtra() {
    }

    static void register(JavaTypes.Builder builder) {
        builder.extension(PluginCommand.class, "setExecutor", function -> function
                .param("executor", CommandExecutor.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    command(arguments).setExecutor(argument(arguments, 1, CommandExecutor.class));
                    return null;
                }));
        builder.extension(PluginCommand.class, "setTabCompleter", function -> function
                .param("completer", TabCompleter.class)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    command(arguments).setTabCompleter(argument(arguments, 1, TabCompleter.class));
                    return null;
                }));
    }

    private static PluginCommand command(Object[] arguments) {
        return argument(arguments, 0, PluginCommand.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
