package com.novalang.bukkit;

import com.novalang.test.TestReport;
import com.novalang.workspace.test.WorkspaceTestRunner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.nio.file.Path;
import java.nio.file.Paths;

/** NovaLang Bukkit 管理命令。测试配置由 Workspace 的 nova.config.yml 提供。 */
public final class NovaPluginCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        if (args.length < 2 || !"test".equalsIgnoreCase(args[0])) {
            sender.sendMessage("用法: /nova test <测试文件|Workspace目录> [--report=<文件>]");
            return true;
        }
        try {
            Path target = Paths.get(args[1]);
            Path reportPath = null;
            for (int index = 2; index < args.length; index++) {
                String option = args[index];
                if (option.startsWith("--report=")) {
                    reportPath = Paths.get(option.substring("--report=".length()));
                } else {
                    sender.sendMessage("未知选项: " + option);
                    return true;
                }
            }
            TestReport report = new WorkspaceTestRunner().run(target, sender::sendMessage);
            if (reportPath != null) {
                report.writeJson(reportPath);
            }
            sender.sendMessage("Nova test: total=" + report.getTotal()
                    + ", passed=" + report.getPassed()
                    + ", failed=" + report.getFailed());
            return true;
        } catch (Exception exception) {
            sender.sendMessage("Nova test 无法启动: " + describeFailure(exception));
            return true;
        }
    }

    private static String describeFailure(Throwable failure) {
        StringBuilder message = new StringBuilder();
        Throwable current = failure;
        while (current != null) {
            if (message.length() > 0) {
                message.append(" -> ");
            }
            message.append(current.getClass().getSimpleName());
            if (current.getMessage() != null && !current.getMessage().isEmpty()) {
                message.append(": ").append(current.getMessage());
            }
            current = current.getCause();
        }
        return message.toString();
    }

}
