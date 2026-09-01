package com.novalang.workspace;

import com.novalang.runtime.SchedulerHolder;
import com.novalang.runtime.interpreter.Interpreter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Creator 热重载时，多虚拟入口共享同一 main 依赖闭包的回归测试。
 *
 * <p>Creator 会把每个 YAML script 包装成导入 main 的虚拟入口。Workspace 必须为整个
 * 模块图构建一份联合编译产物，让 main 及其依赖只出现一次，同时隔离各入口同名函数。</p>
 */
@DisplayName("Workspace 多入口联合编译")
class WorkspaceMultiEntryCompilationMockTest {

    private static final int VIRTUAL_ACTION_COUNT = 20;
    private static final int JAVA_CALL_COUNT = 64;

    @TempDir
    Path tempDirectory;

    @BeforeEach
    void installScheduler() {
        SchedulerHolder.set(WorkspaceTestSupport.directScheduler());
    }

    @AfterEach
    void clearScheduler() {
        Interpreter.resetGlobalSchedulerState();
    }

    @Test
    @DisplayName("清空缓存后整个模块图也只重新编译一次")
    void shouldCompileJointBundleOnceAndRecompileOnceAfterCacheClear() throws Exception {
        String heavyLibrarySource = createJavaInteropLibrary();
        WorkspaceTestSupport.write(tempDirectory, "lib/heavy.nova", heavyLibrarySource);
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "import \"@/lib/heavy\"\n"
                        + "fun mainValue(): Double {\n"
                        + "    return heavy63(-63.0)\n"
                        + "}\n");
        Path configFile = WorkspaceTestSupport.writeConfig(
                tempDirectory, "caller", "  - \"@/main\"\n");
        WorkspaceConfig config = new WorkspaceConfigLoader().load(configFile);

        List<SourceUnit> virtualSources = new ArrayList<SourceUnit>();
        List<String> virtualEntries = new ArrayList<String>();
        Path originFile = WorkspaceTestSupport.write(
                tempDirectory, "template.yml", "script: generated\n");
        for (int index = 0; index < VIRTUAL_ACTION_COUNT; index++) {
            String moduleId = "@generated/action-" + index;
            String source = "import \"@/main\"\n"
                    + "fun execute(): Double {\n"
                    + "    return mainValue() + " + index + ".0\n"
                    + "}\n";
            SourceUnit action = new SourceUnit(moduleId, source, originFile,
                    "stages.action-" + index, index + 1, 0, null);
            virtualSources.add(action);
            virtualEntries.add(moduleId);
        }

        WorkspaceModuleGraph graph = new WorkspaceModuleResolver().resolve(
                config, virtualSources, virtualEntries);
        int expectedEntryCount = VIRTUAL_ACTION_COUNT + 1;
        assertEquals(expectedEntryCount, graph.getEntries().size());

        WorkspaceCompilationBundle bundle = new WorkspaceCompilationBundleBuilder().build(graph);
        assertEquals(1, countOccurrences(bundle.getSource(), "import java java.lang.Math"));
        assertEquals(1, countOccurrences(bundle.getSource(), "fun heavy63(value: Double)"));
        assertEquals(VIRTUAL_ACTION_COUNT,
                countOccurrences(bundle.getSource(), "object __WorkspaceEntry"));

        WorkspaceBytecodeArtifactCache cache = new WorkspaceBytecodeArtifactCache();
        AtomicInteger compilationCount = new AtomicInteger();
        compileJointBundle(configFile, bundle, cache, compilationCount);
        assertEquals(1, compilationCount.get());
        assertEquals(1, cache.size());

        compileJointBundle(configFile, bundle, cache, compilationCount);
        assertEquals(1, compilationCount.get(),
                "保留缓存时相同 Workspace 不应重新编译");

        cache.clear();
        assertEquals(0, cache.size());
        compileJointBundle(configFile, bundle, cache, compilationCount);
        assertEquals(2, compilationCount.get(),
                "宿主清空缓存后，整个模块图只应再次触发一次编译");
    }

    @Test
    @DisplayName("真实 Workspace 将每个模块只放入一次联合编译产物")
    void shouldCompileEachSharedModuleOnlyOnceAcrossVirtualEntries() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "lib/heavy.nova", createJavaInteropLibrary());
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "import \"@/lib/heavy\"\n"
                        + "fun mainValue(): Double {\n"
                        + "    return heavy63(-63.0)\n"
                        + "}\n");
        Path configFile = WorkspaceTestSupport.writeConfig(
                tempDirectory, "caller", "  - \"main\"\n");
        Path originFile = WorkspaceTestSupport.write(
                tempDirectory, "template.yml", "script: generated\n");
        WorkspaceBytecodeArtifactCache cache = new WorkspaceBytecodeArtifactCache();
        RuntimeWorkspace workspace = new RuntimeWorkspace(configFile, nova -> { }, cache);
        for (int index = 0; index < VIRTUAL_ACTION_COUNT; index++) {
            String moduleId = "@generated/action-" + index;
            String source = "import \"@/main\"\n"
                    + "fun execute(): Double {\n"
                    + "    return mainValue() + " + index + ".0\n"
                    + "}\n";
            SourceUnit action = new SourceUnit(moduleId, source, originFile,
                    "stages.action-" + index, index + 1, 0, null);
            workspace.registerVirtualSource(action, true);
        }

        try {
            workspace.load();

            Object first = workspace.invoke("@generated/action-0", "execute",
                    Collections.<String, Object>emptyMap(), null);
            Object last = workspace.invoke("@generated/action-19", "execute",
                    Collections.<String, Object>emptyMap(), null);
            assertEquals(63.0, ((Number) first).doubleValue());
            assertEquals(82.0, ((Number) last).doubleValue());
            assertEquals(VIRTUAL_ACTION_COUNT + 2,
                    workspace.getGeneration().getModuleGraph().getModules().size());
            assertEquals(1, cache.size(),
                    "整个模块图应只生成一份联合字节码产物，公共模块不能按入口重复编译");
        } finally {
            workspace.dispose();
        }
    }

    private String createJavaInteropLibrary() {
        StringBuilder source = new StringBuilder();
        source.append("import java java.lang.Math\n");
        for (int index = 0; index < JAVA_CALL_COUNT; index++) {
            source.append("fun heavy").append(index).append("(value: Double): Double {\n");
            source.append("    return Math.abs(value)\n");
            source.append("}\n");
        }
        return source.toString();
    }

    private void compileJointBundle(Path configFile,
                                    WorkspaceCompilationBundle bundle,
                                    WorkspaceBytecodeArtifactCache cache,
                                    AtomicInteger compilationCount) {
        WorkspaceBytecodeArtifactCache.CacheKey key =
                new WorkspaceBytecodeArtifactCache.CacheKey(
                        getClass().getClassLoader(),
                        configFile.toAbsolutePath().normalize().toString(),
                        "@workspace/joint",
                        bundle.getSource());
        cache.getOrCompile(key, () -> {
            int compilation = compilationCount.incrementAndGet();
            LinkedHashMap<String, byte[]> bytecode = new LinkedHashMap<String, byte[]>();
            bytecode.put("mock.Entry" + compilation, new byte[]{1});
            return bytecode;
        });
    }

    private int countOccurrences(String source, String expected) {
        int count = 0;
        int offset = 0;
        while (true) {
            int found = source.indexOf(expected, offset);
            if (found < 0) {
                return count;
            }
            count++;
            offset = found + expected.length();
        }
    }
}
