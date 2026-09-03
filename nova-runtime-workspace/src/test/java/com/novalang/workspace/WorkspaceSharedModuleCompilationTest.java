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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 多入口引用同一模块时的冷编译回归测试。
 */
@DisplayName("Workspace 共享模块冷编译")
class WorkspaceSharedModuleCompilationTest {

    private static final int ACTION_COUNT = 20;

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
    @DisplayName("共享模块导出的 Nova 构造器属性 getter 必须在编译期被拒绝")
    void shouldRejectMissingExportedNovaPropertyGetter() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "lib/frame.nova",
                "class Frame(val x: Double, val y: Double, val z: Double) { }\n"
                        + "fun createFrame(): Frame { return Frame(1.0, 2.0, 3.0) }\n");
        WorkspaceTestSupport.write(tempDirectory, "lib/consumer.nova",
                "import \"@/lib/frame\"\n"
                        + "fun frameTotal(frame: Frame): Double {\n"
                        + "    return frame.getX() + frame.getY() + frame.getZ()\n"
                        + "}\n");
        WorkspaceTestSupport.write(tempDirectory, "entry-a.nova",
                "import \"@/lib/frame\"\n"
                        + "fun execute(): Double { return createFrame().x }\n");
        WorkspaceTestSupport.write(tempDirectory, "entry-b.nova",
                "import \"@/lib/consumer\"\n"
                        + "fun execute(): Double { return frameTotal(createFrame()) }\n");
        Path configFile = WorkspaceTestSupport.writeConfig(
                tempDirectory, "caller",
                "  - \"entry-a.nova\"\n"
                        + "  - \"entry-b.nova\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(configFile, nova -> { });

        try {
            WorkspaceException exception = assertThrows(WorkspaceException.class, workspace::load);
            String message = describeFailure(exception);
            assertTrue(message.contains("No matching Java method overload found for 'getX'"));
            assertTrue(message.contains("No matching Java method overload found for 'getY'"));
            assertTrue(message.contains("No matching Java method overload found for 'getZ'"));
        } finally {
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("共享模块导出的 Nova 构造器属性可以通过属性语法读取")
    void shouldReadExportedNovaPropertyThroughPropertySyntax() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "lib/frame.nova",
                "class Frame(val x: Double, val y: Double, val z: Double) { }\n"
                        + "fun createFrame(): Frame { return Frame(1.0, 2.0, 3.0) }\n");
        WorkspaceTestSupport.write(tempDirectory, "lib/consumer.nova",
                "import \"@/lib/frame\"\n"
                        + "fun frameTotal(frame: Frame): Double {\n"
                        + "    return frame.x + frame.y + frame.z\n"
                        + "}\n");
        WorkspaceTestSupport.write(tempDirectory, "entry-a.nova",
                "import \"@/lib/frame\"\n"
                        + "fun execute(): Double { return createFrame().x }\n");
        WorkspaceTestSupport.write(tempDirectory, "entry-b.nova",
                "import \"@/lib/consumer\"\n"
                        + "fun execute(): Double { return frameTotal(createFrame()) }\n");
        Path configFile = WorkspaceTestSupport.writeConfig(
                tempDirectory, "caller",
                "  - \"entry-a.nova\"\n"
                        + "  - \"entry-b.nova\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(configFile, nova -> { });

        try {
            workspace.load();
            assertEquals(1.0D, ((Number) workspace.invoke(
                    "entry-a.nova", "execute",
                    Collections.<String, Object>emptyMap(), null)).doubleValue());
            assertEquals(6.0D, ((Number) workspace.invoke(
                    "entry-b.nova", "execute",
                    Collections.<String, Object>emptyMap(), null)).doubleValue());
        } finally {
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("公共模块在同一 Generation 的全部编译输入中只出现一次")
    void shouldIncludeSharedModuleInOnlyOneCompilationInput() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "lib/shared.nova",
                "fun sharedValue(): Int {\n"
                        + "    return 42\n"
                        + "}\n");
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "import \"@/lib/shared\"\n"
                        + "fun mainValue(): Int {\n"
                        + "    return sharedValue()\n"
                        + "}\n");
        Path configFile = WorkspaceTestSupport.writeConfig(
                tempDirectory, "caller", "  - \"@/main\"\n");
        WorkspaceConfig config = new WorkspaceConfigLoader().load(configFile);

        List<SourceUnit> virtualSources = new ArrayList<SourceUnit>();
        List<String> virtualEntries = new ArrayList<String>();
        Path originFile = WorkspaceTestSupport.write(
                tempDirectory, "template.yml", "script: generated\n");
        for (int index = 0; index < ACTION_COUNT; index++) {
            String moduleId = "@generated/action-" + index;
            String source = "import \"@/main\"\n"
                    + "fun execute(): Int {\n"
                    + "    return mainValue() + " + index + "\n"
                    + "}\n";
            virtualSources.add(new SourceUnit(moduleId, source, originFile,
                    "actions." + index, index + 1, 0, null));
            virtualEntries.add(moduleId);
        }

        WorkspaceModuleGraph graph = new WorkspaceModuleResolver().resolve(
                config, virtualSources, virtualEntries);
        WorkspaceCompilationPlan plan = new WorkspaceCompilationPlanner().build(
                graph, configFile.toString());
        WorkspaceCompilationGroupBuilder bundleBuilder =
                new WorkspaceCompilationGroupBuilder();
        Map<String, WorkspaceCompilationExports> exports =
                new LinkedHashMap<String, WorkspaceCompilationExports>();
        int sharedDeclarationOccurrences = 0;
        for (WorkspaceCompilationPlan.Group group : plan.getGroups()) {
            WorkspaceBundle bundle = bundleBuilder.build(graph, group, exports);
            sharedDeclarationOccurrences += countOccurrences(
                    bundle.getSource(), "fun sharedValue(): Int");
            exports.put(group.getId(), WorkspaceCompilationExports.empty());
        }

        assertEquals(ACTION_COUNT + 1, graph.getEntries().size());
        assertEquals(1, sharedDeclarationOccurrences,
                "同一 Generation 内的公共模块不能随每个入口重复进入编译管线");
    }

    @Test
    @DisplayName("入口私有类型和状态隔离且可以调用共享模块")
    void shouldIsolateEntryTypesAndStateWhileLinkingSharedModule() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "lib/shared.nova",
                "import java com.novalang.workspace.SourceUnit\n"
                        + "object SharedValues {\n"
                        + "    fun base(): Int { return 40 }\n"
                        + "    fun zero(): Int { return 0 }\n"
                        + "}\n"
                        + "fun sharedSource(): SourceUnit? { return null }\n"
                        + "fun sharedValue(): Int { return SharedValues.base() }\n");
        WorkspaceTestSupport.write(tempDirectory, "main.nova",
                "import \"@/lib/shared\"\n"
                        + "fun mainValue(): Int { return sharedValue() }\n");
        Path configFile = WorkspaceTestSupport.writeConfig(
                tempDirectory, "caller", "  - \"@/main\"\n");
        Path originFile = WorkspaceTestSupport.write(
                tempDirectory, "template.yml", "script: generated\n");
        WorkspaceBytecodeArtifactCache cache = new WorkspaceBytecodeArtifactCache();
        RuntimeWorkspace workspace = new RuntimeWorkspace(configFile, nova -> { }, cache);
        workspace.registerVirtualSource(new SourceUnit("@generated/first",
                entryWithPrivateRecord(1), originFile, "actions.first", 1, 0, null), true);
        workspace.registerVirtualSource(new SourceUnit("@generated/second",
                entryWithPrivateRecord(2), originFile, "actions.second", 2, 0, null), true);

        try {
            workspace.load();

            assertEquals(42, ((Number) workspace.invoke(
                    "@generated/first", "execute",
                    Collections.<String, Object>emptyMap(), null)).intValue());
            assertEquals(43, ((Number) workspace.invoke(
                    "@generated/first", "execute",
                    Collections.<String, Object>emptyMap(), null)).intValue());
            assertEquals(43, ((Number) workspace.invoke(
                    "@generated/second", "execute",
                    Collections.<String, Object>emptyMap(), null)).intValue());
            assertEquals(3, cache.size(),
                    "公共组一次，加上两个入口私有组，共生成三份模块级产物");
        } finally {
            workspace.dispose();
        }
    }

    @Test
    @DisplayName("只有 import 的编译组作为无操作链接节点加载")
    void shouldLoadImportOnlyCompilationGroupAsNoOpLinkNode() throws Exception {
        WorkspaceTestSupport.write(tempDirectory, "shared.nova",
                "fun sharedValue(): Int { return 42 }\n");
        WorkspaceTestSupport.write(tempDirectory, "entry-a.nova",
                "import \"@/shared\"\n");
        WorkspaceTestSupport.write(tempDirectory, "entry-b.nova",
                "import \"@/shared\"\n"
                        + "fun execute(): Int { return sharedValue() }\n");
        Path configFile = WorkspaceTestSupport.writeConfig(
                tempDirectory, "caller",
                "  - \"entry-a.nova\"\n"
                        + "  - \"entry-b.nova\"\n");
        RuntimeWorkspace workspace = new RuntimeWorkspace(configFile, nova -> { });

        try {
            workspace.load();

            assertEquals(42, ((Number) workspace.invoke(
                    "entry-a.nova", "sharedValue",
                    Collections.<String, Object>emptyMap(), null)).intValue());
            assertEquals(42, ((Number) workspace.invoke(
                    "entry-b.nova", "execute",
                    Collections.<String, Object>emptyMap(), null)).intValue());
        } finally {
            workspace.dispose();
        }
    }

    private String entryWithPrivateRecord(int offset) {
        return "import \"@/main\"\n"
                + "val records = mutableListOf()\n"
                + "class CaptionRecord(val value: Int) {\n"
                + "    fun total(): Int { return value + records.size }\n"
                + "}\n"
                + "fun execute(): Int {\n"
                + "    records.add(" + offset + ")\n"
                + "    val sharedSourceValue: SourceUnit? = sharedSource()\n"
                + "    val record = CaptionRecord(mainValue())\n"
                + "    val zeroSupplier = { SharedValues.zero() }\n"
                + "    return record.total() + " + offset + " + zeroSupplier()\n"
                + "}\n";
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

    private String describeFailure(Throwable failure) {
        StringBuilder description = new StringBuilder();
        Throwable current = failure;
        while (current != null) {
            if (description.length() > 0) {
                description.append(" -> ");
            }
            description.append(current.getClass().getName())
                    .append(": ").append(current.getMessage());
            current = current.getCause();
        }
        return description.toString();
    }
}
