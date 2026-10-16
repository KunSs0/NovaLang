package com.novalang.runtime.compilation;

import com.novalang.runtime.NovaScheduler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;

/**
 * Compilation 单元测试共享文件和调度器工具。
 */
final class CompilationTestSupport {

    private CompilationTestSupport() {
    }

    /**
     * 以 UTF-8 写入测试文件并创建父目录。
     *
     * @param root 测试根目录
     * @param relative 相对文件路径
     * @param content 文件内容
     * @return 写入后的文件路径
     * @throws IOException 文件写入失败
     */
    static Path write(Path root, String relative, String content) throws IOException {
        Path file = root.resolve(relative);
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    /**
     * 写入唯一测试主文件，显式引用本测试需要的模块。
     * @param root 测试文件所在目录
     * @param modules 主文件需要 import 的相对模块路径
     * @return 唯一测试入口文件
     * @throws IOException 文件写入失败
     */
    static Path writeMain(Path root, String... modules) throws IOException {
        StringBuilder source = new StringBuilder();
        for (String module : modules) {
            source.append("import \"./").append(module).append("\"\n");
        }
        return write(root, "test-main.nova", source.toString());
    }

    /**
     * 创建把当前测试线程视为主线程的同步调度器。
     *
     * @return 测试调度器
     */
    static NovaScheduler directScheduler() {
        final Thread owner = Thread.currentThread();
        return new NovaScheduler() {
            @Override
            public Executor mainExecutor() {
                return new Executor() {
                    @Override
                    public void execute(Runnable command) {
                        command.run();
                    }
                };
            }

            @Override
            public Executor asyncExecutor() {
                return new Executor() {
                    @Override
                    public void execute(Runnable command) {
                        command.run();
                    }
                };
            }

            @Override
            public boolean isMainThread() {
                return Thread.currentThread() == owner;
            }

            @Override
            public Cancellable scheduleLater(long delayMs, Runnable task) {
                throw new UnsupportedOperationException("Scheduling is not used by this test");
            }

            @Override
            public Cancellable scheduleRepeat(long delayMs, long periodMs, Runnable task) {
                throw new UnsupportedOperationException("Scheduling is not used by this test");
            }
        };
    }
}
