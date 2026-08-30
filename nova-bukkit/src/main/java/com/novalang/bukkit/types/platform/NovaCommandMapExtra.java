package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.Requires;
import com.novalang.bukkit.types.value.NovaTypeSupport;
import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.util.List;

/** CommandMap 未包含在基础命令表中的可选别名。 */
@Requires(classes = {"org.bukkit.command.CommandMap"})
final class NovaCommandMapExtra {

    private NovaCommandMapExtra() {
    }

    static void register(JavaTypes.Builder builder) {
        JavaTypeRef commandList = JavaTypeRef.listOf(JavaTypeRef.javaType(Command.class));
        JavaTypeRef stringList = JavaTypeRef.listOf(JavaTypeRef.javaType(String.class));
        builder.extension(CommandMap.class, "registerAll", function -> function
                .param("fallbackPrefix", String.class)
                .param("commands", commandList)
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    commandMap(arguments).registerAll(
                            argument(arguments, 1, String.class), commandList(arguments, 2));
                    return null;
                }));
        builder.extension(CommandMap.class, "register", function -> function
                .param("fallbackPrefix", String.class)
                .param("command", Command.class)
                .returns(Boolean.class)
                .invoke(arguments -> commandMap(arguments).register(
                        argument(arguments, 1, String.class), argument(arguments, 2, Command.class))));
        builder.extension(CommandMap.class, "register", function -> function
                .param("label", String.class)
                .param("fallbackPrefix", String.class)
                .param("command", Command.class)
                .returns(Boolean.class)
                .invoke(arguments -> commandMap(arguments).register(
                        argument(arguments, 1, String.class),
                        argument(arguments, 2, String.class),
                        argument(arguments, 3, Command.class))));
        builder.extension(CommandMap.class, "clearCommands", function -> function
                .returns(Void.TYPE)
                .invoke(arguments -> {
                    commandMap(arguments).clearCommands();
                    return null;
                }));
        builder.extension(CommandMap.class, "tabComplete", function -> function
                .param("sender", CommandSender.class)
                .param("commandLine", String.class)
                .returns(stringList.nullable())
                .invoke(arguments -> commandMap(arguments).tabComplete(
                        argument(arguments, 1, CommandSender.class), argument(arguments, 2, String.class))));
        builder.extension(CommandMap.class, "tabComplete", function -> function
                .param("sender", CommandSender.class)
                .param("commandLine", String.class)
                .param("location", Location.class)
                .returns(stringList.nullable())
                .invoke(arguments -> commandMap(arguments).tabComplete(
                        argument(arguments, 1, CommandSender.class),
                        argument(arguments, 2, String.class),
                        argument(arguments, 3, Location.class))));
    }

    private static CommandMap commandMap(Object[] arguments) {
        return argument(arguments, 0, CommandMap.class);
    }

    @SuppressWarnings("unchecked")
    private static List<Command> commandList(Object[] arguments, int index) {
        return argument(arguments, index, List.class);
    }

    private static <T> T argument(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
