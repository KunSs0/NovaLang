package com.novalang.bukkit.types.platform;

import com.novalang.bukkit.types.value.NovaTypeSupport;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Server;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.util.List;

/** 命令和 CommandSender 的 Fluxon 别名。 */
final class NovaCommand {

    private NovaCommand() {
    }

    static void register(JavaTypes.Builder b) {
        b.extension(CommandSender.class, "name", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, CommandSender.class).getName()));
        b.extension(CommandSender.class, "server", f -> f.returns(Server.class).invoke(a -> NovaTypeSupport.argument(a, 0, CommandSender.class).getServer()));
        b.extension(CommandSender.class, "sendMessage", f -> f.param("message", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, CommandSender.class).sendMessage(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        b.extension(CommandSender.class, "sendMessage", f -> f.param("messages", String[].class).invoke(a -> { NovaTypeSupport.argument(a, 0, CommandSender.class).sendMessage(NovaTypeSupport.argument(a, 1, String[].class)); return null; }));
        b.extension(CommandSender.class, "performCommand", f -> f.param("command", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, CommandSender.class).getServer().dispatchCommand(NovaTypeSupport.argument(a, 0, CommandSender.class), NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(BlockCommandSender.class, "block", f -> f.returns(Block.class).invoke(a -> NovaTypeSupport.argument(a, 0, BlockCommandSender.class).getBlock()));
        b.extension(Command.class, "name", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Command.class).getName()));
        b.extension(Command.class, "label", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Command.class).getLabel()));
        b.extension(Command.class, "permission", f -> f.returns(JavaTypeRef.javaType(String.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Command.class).getPermission()));
        b.extension(Command.class, "permissionMessage", f -> f.returns(JavaTypeRef.javaType(String.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, Command.class).getPermissionMessage()));
        b.extension(Command.class, "description", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Command.class).getDescription()));
        b.extension(Command.class, "usage", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, Command.class).getUsage()));
        b.extension(Command.class, "aliases", f -> f.returns(JavaTypeRef.listOf(JavaTypeRef.javaType(String.class))).invoke(a -> NovaTypeSupport.argument(a, 0, Command.class).getAliases()));
        b.extension(Command.class, "isRegistered", f -> f.returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Command.class).isRegistered()));
        b.extension(Command.class, "testPermission", f -> f.param("sender", CommandSender.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Command.class).testPermission(NovaTypeSupport.argument(a, 1, CommandSender.class))));
        b.extension(Command.class, "testPermissionSilent", f -> f.param("sender", CommandSender.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Command.class).testPermissionSilent(NovaTypeSupport.argument(a, 1, CommandSender.class))));
        b.extension(Command.class, "setName", f -> f.param("name", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Command.class).setName(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(Command.class, "setLabel", f -> f.param("label", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, Command.class).setLabel(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(Command.class, "setDescription", f -> f.param("description", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Command.class).setDescription(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        b.extension(Command.class, "setPermission", f -> f.param("permission", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Command.class).setPermission(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        b.extension(Command.class, "setUsage", f -> f.param("usage", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, Command.class).setUsage(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        b.extension(Command.class, "execute", f -> f.param("sender", CommandSender.class).param("label", String.class).param("arguments", List.class).returns(Boolean.class).invoke(a -> command(a).execute(arg(a, 1, CommandSender.class), arg(a, 2, String.class), stringArray(a[3]))));
        b.extension(Command.class, "tabComplete", f -> f.param("sender", CommandSender.class).param("alias", String.class).param("arguments", List.class).returns(JavaTypeRef.listOf(JavaTypeRef.javaType(String.class))).invoke(a -> command(a).tabComplete(arg(a, 1, CommandSender.class), arg(a, 2, String.class), stringArray(a[3]))));
        b.extension(Command.class, "tabComplete", f -> f.param("sender", CommandSender.class).param("alias", String.class).param("arguments", List.class).param("location", Location.class).returns(JavaTypeRef.listOf(JavaTypeRef.javaType(String.class))).invoke(a -> command(a).tabComplete(arg(a, 1, CommandSender.class), arg(a, 2, String.class), stringArray(a[3]), arg(a, 4, Location.class))));
        b.extension(Command.class, "register", f -> f.param("commandMap", CommandMap.class).returns(Boolean.class).invoke(a -> command(a).register(arg(a, 1, CommandMap.class))));
        b.extension(Command.class, "unregister", f -> f.param("commandMap", CommandMap.class).returns(Boolean.class).invoke(a -> command(a).unregister(arg(a, 1, CommandMap.class))));
        b.extension(Command.class, "setAliases", f -> f.param("aliases", List.class).returns(Command.class).invoke(a -> command(a).setAliases(stringList(a[1]))));
        b.extension(Command.class, "setPermissionMessage", f -> f.param("message", JavaTypeRef.javaType(String.class).nullable()).returns(Command.class).invoke(a -> command(a).setPermissionMessage(arg(a, 1, String.class))));
        b.extension(Command.class, "broadcastCommandMessage", f -> f.param("source", CommandSender.class).param("message", String.class).invoke(a -> { Command.broadcastCommandMessage(arg(a, 1, CommandSender.class), arg(a, 2, String.class)); return null; }));
        b.extension(Command.class, "broadcastCommandMessage", f -> f.param("source", CommandSender.class).param("message", String.class).param("sendToSource", Boolean.class).invoke(a -> { Command.broadcastCommandMessage(arg(a, 1, CommandSender.class), arg(a, 2, String.class), arg(a, 3, Boolean.class)); return null; }));
        b.extension(Command.class, "toString", f -> f.returns(String.class).invoke(a -> command(a).toString()));
        b.extension(PluginCommand.class, "plugin", f -> f.returns(Plugin.class).invoke(a -> NovaTypeSupport.argument(a, 0, PluginCommand.class).getPlugin()));
        b.extension(PluginCommand.class, "executor", f -> f.returns(JavaTypeRef.javaType(org.bukkit.command.CommandExecutor.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, PluginCommand.class).getExecutor()));
        b.extension(PluginCommand.class, "tabCompleter", f -> f.returns(JavaTypeRef.javaType(TabCompleter.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, PluginCommand.class).getTabCompleter()));
        b.extension(PluginIdentifiableCommand.class, "plugin", f -> f.returns(Plugin.class).invoke(a -> NovaTypeSupport.argument(a, 0, PluginIdentifiableCommand.class).getPlugin()));
        b.extension(ProxiedCommandSender.class, "caller", f -> f.returns(CommandSender.class).invoke(a -> NovaTypeSupport.argument(a, 0, ProxiedCommandSender.class).getCaller()));
        b.extension(ProxiedCommandSender.class, "callee", f -> f.returns(CommandSender.class).invoke(a -> NovaTypeSupport.argument(a, 0, ProxiedCommandSender.class).getCallee()));
        b.extension(CommandMap.class, "getCommand", f -> f.param("name", String.class).returns(JavaTypeRef.javaType(Command.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, CommandMap.class).getCommand(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(CommandMap.class, "dispatch", f -> f.param("sender", CommandSender.class).param("commandLine", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, CommandMap.class).dispatch(NovaTypeSupport.argument(a, 1, CommandSender.class), NovaTypeSupport.argument(a, 2, String.class))));
    }

    private static Command command(Object[] arguments) {
        return arg(arguments, 0, Command.class);
    }

    @SuppressWarnings("unchecked")
    private static List<String> stringList(Object value) {
        return (List<String>) value;
    }

    private static String[] stringArray(Object value) {
        List<String> values = stringList(value);
        return values.toArray(new String[values.size()]);
    }

    private static <T> T arg(Object[] arguments, int index, Class<T> type) {
        return NovaTypeSupport.argument(arguments, index, type);
    }
}
