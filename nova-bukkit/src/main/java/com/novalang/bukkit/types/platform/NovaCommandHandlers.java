package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

/** 命令执行与补全回调的可选 Fluxon 别名。 */
@Requires(classes = {
        "org.bukkit.command.CommandExecutor",
        "org.bukkit.command.TabCompleter"
})
final class NovaCommandHandlers {

    private NovaCommandHandlers() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef stringList = JavaTypeRef.listOf(JavaTypeRef.javaType(String.class));
        JavaTypeRef nullableStringList = stringList.nullable();
        builder.extension(CommandExecutor.class, "onCommand", function -> function
                .param("sender", CommandSender.class)
                .param("command", Command.class)
                .param("label", String.class)
                .param("arguments", stringList)
                .returns(Boolean.class)
                .invoke(arguments -> commandExecutor(arguments).onCommand(
                        argument(arguments, 1, CommandSender.class),
                        argument(arguments, 2, Command.class),
                        argument(arguments, 3, String.class),
                        stringArray(arguments, 4))));
        builder.extension(TabCompleter.class, "onTabComplete", function -> function
                .param("sender", CommandSender.class)
                .param("command", Command.class)
                .param("alias", String.class)
                .param("arguments", stringList)
                .returns(nullableStringList)
                .invoke(arguments -> tabCompleter(arguments).onTabComplete(
                        argument(arguments, 1, CommandSender.class),
                        argument(arguments, 2, Command.class),
                        argument(arguments, 3, String.class),
                        stringArray(arguments, 4))));
    }

    private static CommandExecutor commandExecutor(Object[] arguments) {
        return argument(arguments, 0, CommandExecutor.class);
    }

    private static TabCompleter tabCompleter(Object[] arguments) {
        return argument(arguments, 0, TabCompleter.class);
    }

    @SuppressWarnings("unchecked")
    private static String[] stringArray(Object[] arguments, int index) {
        List<String> values = argument(arguments, index, List.class);
        return values.toArray(new String[values.size()]);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
