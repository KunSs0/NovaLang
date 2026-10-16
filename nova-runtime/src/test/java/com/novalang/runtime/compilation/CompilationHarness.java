package com.novalang.runtime.compilation;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import com.novalang.runtime.interpreter.ModuleLoader;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 模块编译回归测试的输入准备工具。
 * <p>只简化测试文件配置和初始化步骤；实际执行和资源关闭均交给普通 CompiledNova。</p>
 */
final class CompilationHarness implements AutoCloseable {
    private final Path entryFile;
    private final Nova nova = new Nova();
    private final CompilationConfig.Builder metadata;
    private final BytecodeArtifactCache cache;
    private CompiledNova program;
    private final List<String> registeredModules = new ArrayList<>();

    /** 使用独立字节码缓存准备测试。 */
    CompilationHarness(Path entryFile, Consumer<Nova> host) {
        this(entryFile, host, new BytecodeArtifactCache());
    }

    /** 安装测试绑定并为唯一测试入口配置路径别名。 */
    CompilationHarness(Path entryFile, Consumer<Nova> host, BytecodeArtifactCache cache) {
        this.cache = cache;
        this.entryFile = entryFile;
        metadata = new CompilationConfig.Builder();
        metadata.alias("@", entryFile.toAbsolutePath().getParent());
        nova.setScriptClassLoader(CompilationHarness.class.getClassLoader());
        host.accept(nova);
    }

    /**
     * 通过模块系统登记依赖，并在测试主文件里显式添加 import。
     * @param moduleId 导入使用的模块标识
     * @param source 模块源码
     * @throws IOException 测试主文件读写失败
     */
    void importModule(String moduleId, String source) throws IOException {
        ModuleLoader.registerSharedModule(moduleId, source);
        registeredModules.add(moduleId);
        String main = new String(Files.readAllBytes(entryFile), StandardCharsets.UTF_8);
        String imported = "import \"" + moduleId + "\"\n" + main;
        Files.write(entryFile, imported.getBytes(StandardCharsets.UTF_8));
    }

    /** 编译并显式初始化程序，失败时关闭已创建的程序。 */
    void compileAndRun() {
        program = nova.compileToBytecode(entryFile, metadata.build(), cache);
        try {
            program.run();
        } catch (RuntimeException failure) {
            try {
                program.close();
            } catch (RuntimeException cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }
            throw failure;
        }
    }

    /** 使用普通 Nova 的独立调用绑定执行测试函数。 */
    Object call(String function, Map<String, Object> bindings, Object... arguments) {
        return program.callIsolated(function, bindings, arguments);
    }

    /** @return 当前测试实际编译出的普通 Nova 程序 */
    CompiledNova getProgram() {
        return program;
    }

    /** 关闭程序，并注销本测试通过模块系统登记的依赖。 */
    @Override
    public void close() {
        try {
            if (program != null) {
                program.close();
            }
        } finally {
            // 即使程序清理失败，也不能把测试模块泄漏给后续测试。
            for (String moduleId : registeredModules) {
                ModuleLoader.unregisterSharedModule(moduleId);
            }
            registeredModules.clear();
        }
    }
}
