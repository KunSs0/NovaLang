package com.novalang.bukkit;

import com.novalang.mock.MockTestReport;
import com.novalang.mock.MockTestRunner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** NovaLang Bukkit 管理命令，当前提供 mock 单元测试流程。 */
public final class NovaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        if (args.length < 2 || !"mock".equalsIgnoreCase(args[0])) {
            sender.sendMessage("用法: /nova mock <xxx.mock.nova|目录> [--report=<文件>]");
            return true;
        }
        try {
            Path target = Paths.get(args[1]);
            List<Path> sourceRoots = new ArrayList<Path>();
            Map<String, Path> aliases = new LinkedHashMap<String, Path>();
            Map<String, Object> mocks = new LinkedHashMap<String, Object>();
            Path reportPath = null;
            for (int index = 2; index < args.length; index++) {
                String option = args[index];
                if (option.startsWith("--source-root=")) {
                    sourceRoots.add(Paths.get(option.substring("--source-root=".length())));
                } else if (option.startsWith("--alias=")) {
                    parseAlias(option.substring("--alias=".length()), aliases);
                } else if (option.startsWith("--mock=")) {
                    parseMock(option.substring("--mock=".length()), mocks);
                } else if (option.startsWith("--report=")) {
                    reportPath = Paths.get(option.substring("--report=".length()));
                } else {
                    sender.sendMessage("未知选项: " + option);
                    return true;
                }
            }
            MockTestReport report = new MockTestRunner().run(
                    target, sourceRoots, aliases, mocks,
                    new BukkitMockTestHost(), sender::sendMessage);
            if (reportPath != null) {
                report.writeJson(reportPath);
            }
            sender.sendMessage("Nova mock: total=" + report.getTotal()
                    + ", passed=" + report.getPassed()
                    + ", failed=" + report.getFailed());
            return true;
        } catch (Exception exception) {
            sender.sendMessage("Nova mock 无法启动: " + describeFailure(exception));
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

    private static void parseAlias(String text, Map<String, Path> aliases) {
        int separator = text.indexOf('=');
        if (separator <= 0) {
            throw new IllegalArgumentException("alias 格式必须为 @name=目录");
        }
        aliases.put(text.substring(0, separator), Paths.get(text.substring(separator + 1)));
    }

    private static void parseMock(String text, Map<String, Object> mocks) {
        int separator = text.indexOf('=');
        if (separator <= 0) {
            throw new IllegalArgumentException("mock 格式必须为 name=value");
        }
        String name = text.substring(0, separator);
        String value = text.substring(separator + 1);
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            mocks.put(name, Boolean.valueOf(value));
            return;
        }
        try {
            mocks.put(name, Integer.valueOf(value));
            return;
        } catch (NumberFormatException ignored) {
            // 保留为字符串 mock 值。
        }
        mocks.put(name, value);
    }
}
