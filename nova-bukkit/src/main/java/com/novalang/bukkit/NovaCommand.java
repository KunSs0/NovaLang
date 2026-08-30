package com.novalang.bukkit;

import com.novalang.runtime.host.JavaTypeRef;
import com.novalang.runtime.host.JavaTypes;
import org.bukkit.Server;
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

/** 命令和 CommandSender 的 Fluxon 别名。 */
final class NovaCommand {

    private NovaCommand() {
    }

    static void register(JavaTypes.Builder b) {
        b.extension(CommandSender.class, "name", f -> f.returns(String.class).invoke(a -> NovaTypeSupport.argument(a, 0, CommandSender.class).getName()));
        b.extension(CommandSender.class, "server", f -> f.returns(Server.class).invoke(a -> NovaTypeSupport.argument(a, 0, CommandSender.class).getServer()));
        b.extension(CommandSender.class, "sendMessage", f -> f.param("message", String.class).invoke(a -> { NovaTypeSupport.argument(a, 0, CommandSender.class).sendMessage(NovaTypeSupport.argument(a, 1, String.class)); return null; }));
        b.extension(CommandSender.class, "sendMessage", f -> f.param("messages", String[].class).invoke(a -> { NovaTypeSupport.argument(a, 0, CommandSender.class).sendMessage(NovaTypeSupport.argument(a, 1, String[].class)); return null; }));
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
        b.extension(PluginCommand.class, "plugin", f -> f.returns(Plugin.class).invoke(a -> NovaTypeSupport.argument(a, 0, PluginCommand.class).getPlugin()));
        b.extension(PluginCommand.class, "executor", f -> f.returns(JavaTypeRef.javaType(org.bukkit.command.CommandExecutor.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, PluginCommand.class).getExecutor()));
        b.extension(PluginCommand.class, "tabCompleter", f -> f.returns(JavaTypeRef.javaType(TabCompleter.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, PluginCommand.class).getTabCompleter()));
        b.extension(PluginIdentifiableCommand.class, "plugin", f -> f.returns(Plugin.class).invoke(a -> NovaTypeSupport.argument(a, 0, PluginIdentifiableCommand.class).getPlugin()));
        b.extension(ProxiedCommandSender.class, "caller", f -> f.returns(CommandSender.class).invoke(a -> NovaTypeSupport.argument(a, 0, ProxiedCommandSender.class).getCaller()));
        b.extension(ProxiedCommandSender.class, "callee", f -> f.returns(CommandSender.class).invoke(a -> NovaTypeSupport.argument(a, 0, ProxiedCommandSender.class).getCallee()));
        b.extension(CommandMap.class, "getCommand", f -> f.param("name", String.class).returns(JavaTypeRef.javaType(Command.class).nullable()).invoke(a -> NovaTypeSupport.argument(a, 0, CommandMap.class).getCommand(NovaTypeSupport.argument(a, 1, String.class))));
        b.extension(CommandMap.class, "dispatch", f -> f.param("sender", CommandSender.class).param("commandLine", String.class).returns(Boolean.class).invoke(a -> NovaTypeSupport.argument(a, 0, CommandMap.class).dispatch(NovaTypeSupport.argument(a, 1, CommandSender.class), NovaTypeSupport.argument(a, 2, String.class))));
    }
}
