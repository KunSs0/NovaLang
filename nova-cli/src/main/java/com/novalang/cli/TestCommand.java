package com.novalang.cli;

import com.novalang.test.TestReport;
import com.novalang.workspace.test.WorkspaceTestRunner;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/** nova test 子命令：执行 Workspace 目录下的 Nova 测试。 */
@Command(name = "test", description = "执行 Workspace Nova 单元测试",
        mixinStandardHelpOptions = true)
public final class TestCommand implements Callable<Integer> {

    @Parameters(index = "0", description = "测试文件或 Workspace 目录")
    private String target;

    @Option(names = "--report", description = "输出 JSON 测试报告")
    private Path report;

    @Override
    public Integer call() {
        TestReport result = new WorkspaceTestRunner().run(
                Paths.get(target), System.out::println);
        if (report != null) {
            try {
                result.writeJson(report);
            } catch (Exception exception) {
                System.err.println("无法写入测试报告: " + exception.getMessage());
                return 1;
            }
        }
        System.out.println("Nova test: total=" + result.getTotal()
                + ", passed=" + result.getPassed()
                + ", failed=" + result.getFailed());
        return result.getFailed() == 0 ? 0 : 1;
    }
}
